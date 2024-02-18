import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MarkdownProcessor {
    
    private static final char HEADER_SYMBOL = '#';
    private static final char LINK_OPEN = '[';
    private static final char LINK_CLOSE = ']';
    private static final char HREF_OPEN = '(';
    private static final char HREF_CLOSE = ')';

    public static void processMarkdown(String fileName) {
        try (BufferedReader markdownFile = new BufferedReader(new FileReader(fileName));
            BufferedWriter generatedHtml = new BufferedWriter(new FileWriter("myhtml.html"))) {
            String line;

            while ((line = markdownFile.readLine()) != null) {
                if(line.length()==0)
                    continue;
                boolean readingHeaderSize = false, readingHeader = false, readingParagraph = false, readingLink = false, readingHref = false;
                String linkText = "", htmlLine = "", href="";
                int headerSize = 0;    

                for (char character : line.toCharArray()) {
                    switch (character) {
                        case '#':
                            readingHeaderSize = true;
                            headerSize++;
                            break;
                        case '[':
                            generatedHtml.write("<a href=\"");
                            readingLink = true;
                            break;
                        case ']':
                            readingLink = false;
                            break;
                        case '(':
                            readingHref = true;                            
                            break;
                        case ')':
                            if (readingHref) {
                                generatedHtml.write(href + "\">" + linkText + "</a>");
                                readingHref = false;
                            }
                            break;
                        //If the character is not one of the keys
                        default:
                            if (readingLink) {
                                linkText += character;
                            } 
                            else if(readingHref){
                                href += character;
                            }
                            else if (readingHeaderSize && character == ' ') {
                                generatedHtml.write("<h" + headerSize + ">");
                                readingHeaderSize = false;
                                readingHeader = true;
                            } 
                            else if (!readingHeader && !readingParagraph && !readingLink && !readingHref && !readingHeaderSize && character != ' ') {
                                generatedHtml.write("<p>"+character);
                                readingParagraph = true;
                            }
                            else {
                                generatedHtml.write(character);
                            }
                            break;
                    }
                }

                if (readingHeader) {
                    generatedHtml.write("</h" + headerSize + ">");
                } 
                if(readingParagraph) {
                    generatedHtml.write("</p>");
                }
                generatedHtml.write("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        processMarkdown("longerTest.md");
    }
}

