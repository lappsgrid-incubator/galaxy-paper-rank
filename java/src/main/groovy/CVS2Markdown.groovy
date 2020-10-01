/**
 * A program that reads a CSV file and prints it out at a table in
 * GitHub flavored Markdown.
 *
 * Currently the display and sort options are hard coded.
 */
class CVS2Markdown {

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

        Comparator<String[]> comparator = new RowComparator()
        comparator.add(new CellComparator(3,).ascending().string())
        comparator.add(new CellComparator(1).descending().numeric())
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
            if (i != 2) {
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
            if (i != 2) {
                writer.print(pad(cell, widths[i]))
                writer.print("|")
            }
        }
        println writer.toString()
    }

    static void main(String[] args) {
        if (args.length == 0) {
            // new CVS2Markdown().process("/Users/suderman/Projects/identify-galaxy/publishers.csv")
            println "USAGE: groovy CSV2Markdown.groovy /path/to/file.csv"
            return
        }

        new CVS2Markdown().process(args[0])
    }

    class CellComparator implements Comparator<String[]> {
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

    class RowComparator implements Comparator<String[]> {

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
