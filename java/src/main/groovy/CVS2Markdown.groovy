@GrabConfig(systemClassLoader=true)
// NOTE: You may have to tweak this version number depending on the version of
// Groovy you are using.
@Grab("org.codehaus.groovy:groovy-cli-picocli:3.0.5")
import groovy.cli.picocli.CliBuilder

/**
 * A program that reads a CSV file and prints it out as a table in
 * GitHub flavored Markdown.
 *
 *
 */
class CVS2Markdown {

    RowComparator comparator
    Set<Integer> skipColumns

    void process(String path) {
        process(new File(path))
    }

    void process(File file) {
        if (!file.exists()) {
            println "File not found ${file.path}"
            return
        }

        List<String[]> table = []
        String[] headers
        int[] widths

        file.withReader { reader ->
            String line = reader.readLine()
            headers = line.split(",")
            widths = new int[headers.length]

            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",")
                if (row.length != headers.length) {
                    throw new IOException("Invalid row in table: $line")
                }
                row.eachWithIndex { String entry, int i ->
                    if (entry.length() > widths[i]) {
                        widths[i] = entry.length()
                    }

                }
                table.add(row)
            }
        }

        Collections.sort(table, comparator)
        printRow(headers, widths)
        printRuler(widths)
        table.each { printRow(it, widths) }

    }

    String pad(String s, int width) {
        String fmt = " %-${width}s "
        return String.format(fmt, s)
    }


    void printRuler(int[] widths) {
        StringWriter writer = new StringWriter()
        writer.print("|")
        widths.eachWithIndex { int w, int i ->
            if (!skipColumns.contains(i)) {
                String fmt = " %${w}s "
                writer.print(String.format(fmt, " ").replaceAll(" ","-"))
                writer.print("|")
            }
        }
        println writer.toString()
    }
    void printRow(String[] row, int[] widths) {
        StringWriter writer = new StringWriter()
        writer.print("|")
        row.eachWithIndex { String cell, int i ->
            if (!skipColumns.contains(i)) {
                writer.print(pad(cell, widths[i]))
                writer.print("|")
            }
        }
        println writer.toString()
    }

    static void main(String[] args) {
        CliBuilder cli = new CliBuilder(usage:'CVS2Markdown', stopAtNonOption: false)
        cli.x(longOpt:'skip', valueSeparator:',', args:'+', argName:'N', optionalArg:true, 'columns to omit in the output')
        cli.s(longOpt:'sort', optionalArg: true, type:String, args:'0..*', argName:'#[AD][NS]', 'sort options')
        cli.usageMessage.footer '''
The sort option expects a three part string:
- the column number
- either an A (ascending) or D (descending) sort
- either an N (numberic) or S (String) sort

The parts can appear in any order and only the column number is required. The ANDS can be upper or lower case.

@|bold EXAMPLES|@
$>groovy CVS2Markdown.groovy --sort 3AS --sort nd4 ...
'''
        def options = cli.parse(args)
        if (!options) {
            cli.usage()
            return
        }
        List<String> files = options.arguments()
        if (files == null || files.size() == 0) {
            println "No CSV file provided."
            cli.usage()
            return
        }

        CVS2Markdown app = new CVS2Markdown()
        app.skipColumns = new TreeSet<>()
        if (options.xs) {
            options.xs.each { app.skipColumns.add(Integer.valueOf(it)) }
        }
        app.comparator = new CVS2Markdown.RowComparator()
        if (options.ss) {
            options.ss.each { String spec ->
                CellComparator cc = SortSpecParser.parse(spec)
                if (cc == null) {
                    return
                }
                app.comparator.add(cc)
            }
        }
        else {
            app.comparator.add(new CVS2Markdown.CellComparator(0, false, false))
        }

        app.process(files[0])
    }

    static class CellComparator implements Comparator<String[]> {
        int column
        boolean ascending
        boolean numeric

        CellComparator(int column, boolean ascending = true, boolean numeric = false) {
            this.column = column
            this.ascending = ascending
            this.numeric = numeric
        }

        CellComparator ascending() { ascending = true ; this }
        CellComparator descending() { ascending = false ; this }
        CellComparator numeric() { numeric = true ; this }
        CellComparator string() { numeric = false ; this }

        @Override
        int compare(String[] aRow, String[] bRow) {
            String a = aRow[column]
            String b = bRow[column]
            if (ascending) {
                if (numeric) {
                    return (a as int) - (b as int)
                }
                return a.compareTo(b)
            }
            else {
                if (numeric) {
                    return (b as int) - (a as int)
                }
                return b.compareTo(a)
            }
            return 0
        }


    }

    static class SortSpecParser {
        static CellComparator parse(String spec) {
            boolean foundDigit = false;
            int col = 0
            boolean isAscending = true
            boolean isNumeric = false
            char[] chars = spec.toCharArray()
            char zero = '0'.charAt(0)
            for (int i = 0; i < chars.length; ++i) {
                if (Character.isDigit(chars[i])) {
                    col = col * 10 + chars[i] - zero
                    foundDigit = true
                }
                else {
                    switch (chars[i]) {
                        case 'N':
                        case 'n':
                            isNumeric = true;
                            break
                        case 'S':
                        case 's':
                            isNumeric = false;
                            break
                        case 'A': case 'a':
                            isAscending = true
                            break
                        case 'D': case 'd':
                            isAscending = false
                            break
                        default:
                            println "Invalid sort configuration: $spec"
                            return null
                    }
                }
            }
            if (!foundDigit) {
                return null;
            }
            return new CellComparator(col, isAscending, isNumeric)
        }
    }
    static class RowComparator implements Comparator<String[]> {

        List<CellComparator> comparators = []

        void add(CellComparator comparator) {
            comparators.add(comparator)
        }

        void add(int column, boolean ascending=true, boolean numeric=false) {
            comparators.add(new CellComparator(column, ascending, numeric))
        }

        @Override
        int compare(String[] a, String[] b) {
            int result = 0
            for (Comparator<String[]> comparator : comparators) {
                result = comparator.compare(a, b)
                if (result != 0) return result
            }
            return result
        }
    }
}
