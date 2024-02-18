import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class MarkdownProcessor{
    
    private static final char HEADER_SYMBOL = '#';
    private static final char LINK_OPEN = '[';
    private static final char LINK_CLOSE = ']';
    private static final char HREF_OPEN = '(';
    private static final char HREF_CLOSE = ')';

    public static String processMarkdown(String inputFileName, int numRuns) throws FileNotFoundException, IOException {

        //If the program has been run more than once, ensure to add an index to the output file name.
        String outputFileName = "myHTML", line = "";
        if(numRuns > 0){
            outputFileName += Integer.toString(numRuns);
        }

        BufferedReader markdownInputFile = new BufferedReader(new FileReader(inputFileName));
        BufferedWriter htmlOutputFile = new BufferedWriter(new FileWriter(outputFileName += ".html"));;

        while ((line = markdownInputFile.readLine()) != null) {
            if(line.length()==0){
                continue;
            }

            boolean readingHeaderSize = false, readingHeader = false, readingParagraph = false, readingLink = false, readingHref = false;
            String linkText = "", htmlLine = "", href="";
            int headerSize = 0;    

            for (char character : line.toCharArray()) {
                switch (character) {
                    case HEADER_SYMBOL:
                        readingHeaderSize = true;
                        headerSize++;
                        break;
                    case LINK_OPEN:
                        htmlOutputFile.write("<a href=\"");
                        readingLink = true;
                        break;
                    case LINK_CLOSE:
                        readingLink = false;
                        break;
                    case HREF_OPEN:
                        readingHref = true;                            
                        break;
                    case HREF_CLOSE:
                        if (readingHref) {
                            htmlOutputFile.write(href + "\">" + linkText + "</a>");
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
                            htmlOutputFile.write("<h" + headerSize + ">");
                            readingHeaderSize = false;
                            readingHeader = true;
                        } 
                        else if (!readingHeader && !readingParagraph && !readingLink && !readingHref && !readingHeaderSize && character != ' ') {
                            htmlOutputFile.write("<p>"+character);
                            readingParagraph = true;
                        }
                        else {
                            htmlOutputFile.write(character);
                        }
                        break;
                }
            }

            if (readingHeader) {
                htmlOutputFile.write("</h" + headerSize + ">");
            } 
            if(readingParagraph) {
                htmlOutputFile.write("</p>");
            }
            htmlOutputFile.write("\n");
        }
        markdownInputFile.close();
        htmlOutputFile.close();
        return outputFileName;
    }

    public static void main(String[] args) {
        int numRuns = 0;
        String inputStr = "";
        Scanner scanner = new Scanner(System.in);  // Create a Scanner object

        System.out.println("\nWelcome to my Markdown to HTML Converter! Enter 'Q' at any time to exit.");
        
        while(inputStr != "Q"){
            System.out.print("\nEnter a markdown filename to convert to HTML (extension must be '.md'): ");
        
            inputStr = scanner.next(); 

            if(inputStr.equals("Q")){
               break;
            }

            //Validate user input - if an empty filename/non-MD file is given, re-prompt user.
            else if(inputStr == null || inputStr.length() == 0){
                System.out.println("Please input a valid file name.");
                continue;
            }else if(!(inputStr.contains(".md"))){ 
                System.out.println("Please input a valid file with extension '.md'.");
                continue;
            }
        
            try {
                String outputFileName = processMarkdown(inputStr, numRuns);
                System.out.println("Success! View your HTML file in " + outputFileName);
            }
            catch(FileNotFoundException fileNotFound){
                System.out.println("\nA file with the given name was not found in this directory.\n");
            }
            catch(IOException e){
                System.out.println("IOException");
            }
            numRuns++;
        }
        scanner.close();
    }
}

