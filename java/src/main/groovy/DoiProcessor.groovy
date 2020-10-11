@GrabConfig(systemClassLoader=true)
// NOTE: You may have to tweak this version number depending on the version of
// Groovy you are using.
@Grab("org.codehaus.groovy:groovy-cli-picocli:3.0.5")
import groovy.cli.picocli.CliBuilder

import java.nio.file.Files

/**
 * A command line program that walks a directory tree parsing all DOI/XML files
 * found.  The program can print two summaries of the DOI files.
 * 1.
 */
class DoiProcessor {

    boolean checkDir(File directory, String type) {
        if (!directory.exists()) {
            println "$type not found"
            return false
        }
        if (!directory.isDirectory()) {
            println "$type is not a directory"
            return false
        }
        return true
    }

    XmlFileVisitor walk(File directory) {
        XmlFileVisitor visitor = new XmlFileVisitor()
        Files.walkFileTree(directory.toPath(), visitor)
        return visitor
    }

    void publishers(File directory, File outdir) {
        if (!checkDir(directory, "Input") || !checkDir(outdir, "Output")) {
            return
        }
        XmlFileVisitor visitor = walk(directory)
        visitor.publisherIndex.sort().each { host,list ->
            File outfile = new File(outdir, "${host}.csv")
            outfile.withPrintWriter { writer ->
                list.each { writer.println "${it.url()}" }
            }
            println "Wrote ${outfile.path} ${list.size()}"
        }
    }

    void summary(File directory) {
        if (!checkDir(directory, "Input")) {
            return
        }
        XmlFileVisitor visitor = walk(directory)
        println("Publisher,Size,Type")
        visitor.publisherIndex.each { host, list ->
            Map<String,Counter> counters = [:]
            list.each { record ->
                record.types.each { type,url ->
                    Counter counter = counters[type]
                    if (counter == null) {
                        counter = new Counter()
                        counters[type] = counter
                    }
                    ++counter
                }
            }
            counters.each { type,counter ->
               println("$host,${counter.count},$type")
            }
        }
    }

    void types(File directory) {
        if (!checkDir(directory,"Input")) {
            return
        }
        XmlFileVisitor visitor = walk(directory)
        visitor.typeIndex.each { String type, List<Record> records ->
            println type
            records.each { println it.url(type) }
            println()
        }
    }

    static void main(String[] args) {
        CliBuilder cli = new CliBuilder(stopAtNonOption: false)
        cli.p(longOpt:'publishers', 'saves a file index for each publisher')
        cli.s(longOpt:'summary', 'prints a summary of types available from each publisher')
        cli.t(longOpt:'types', 'prints files sorted by type')
//        cli.i(longOpt:'in', args:1, type:File, optionalArg: false, argName:'DIR', 'input directory of DOI files')
//        cli.o(longOpt:'out', args:1, type:File, optionalArg: true, argName:'DIR', 'output directory')
        cli.h(longOpt:'help', 'display this help message')
        def options = cli.parse(args)
        if (!options) {
            println "Invalid options."
            cli.usage()
            return
        }
        if (options.h) {
            cli.usage()
            return
        }

        int nargs = 0
        if (options.p) ++nargs
        if (options.t) ++nargs
        if (options.s) ++nargs
        if (nargs == 0) {
            println "One of -p -s or -t must be specified."
            cli.usage()
            return
        }
        if (nargs != 1) {
            println "Only one of -p -s or -t may be specified"
            cli.usage()
            return
        }
        List<String> argv = options.arguments()
        if (options.p) {
            if (argv.size() != 2) {
                println "Input and output directories are required"
                cli.usage()
                return
            }
            File indir = new File(argv[0])
            File outdir = new File(argv[1])
            new DoiProcessor().publishers(indir, outdir)
        }
        else if (options.t) {
            if (argv.size() != 1) {
                println "Input directory required"
                cli.usage()
                return
            }
            new DoiProcessor().types(new File(argv[0]))
        }
        else if (options.s) {
            if (argv.size() != 1) {
                println "Input directory required"
                cli.usage()
                return
            }
            new DoiProcessor().summary(new File(argv[0]))
        }
        else {
            println "You must specify one of -p -s or -t"
        }
    }

    static class Counter {
        int count

        Counter next() {
            ++count
            return this
        }
        Counter prev() {
            --count
            return this
        }
    }
}
