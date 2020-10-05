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
        cli.p(longOpt:'publishers', args:1, optionalArg:true, 'saves a file index for each publisher')
        cli.t(longOpt:'types', args:1, optionalArg:true, 'prints files sorted by type')
        cli.i(longOpt:'in', args:1, type:File, optionalArg: false, argName:'DIR', 'input directory of DOI files')
        cli.o(longOpt:'out', args:1, type:File, optionalArg: true, argName:'DIR', 'output directory')
        def options = cli.parse(args)
        if (!options) {
            println "Invalid options."
            cli.usage()
            return
        }
        if (options.p && options.t) {
            println "You can only specify one of -p or -t"
            return
        }
        if (options.p) {
            if (!options.o) {
                println "An output directoyr must be specified with -p"
                return
            }
            new DoiProcessor().publishers(options.i, options.o)
        }
        else if (options.t) {
            new DoiProcessor().types(options.i)
        }
        else {
            println "You must specify one of -p or -t (but not both)"
        }
    }
}
