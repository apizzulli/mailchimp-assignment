import java.io.*;
import java.util.*;

/**
* This is a Markdown to HTML file converter. The user will input a Markdown file and receive   
* an HTML file, converted according to the specifications described in 'README.md,' which
* can be viewed in the browser.
*
* Run 'java MarkdownProcessor.java' in the command line in the same directory as this file to start.
*
* @author Anthony Pizzulli
*/

public class MarkdownProcessor{
    
    private static final char HEADER_OPEN = '#';
    private static final char LINK_OPEN = '[';
    private static final char LINK_CLOSE = ']';
    private static final char HREF_OPEN = '(';
    private static final char HREF_CLOSE = ')';
    private static final char EMPTY_SPACE = ' ';
    private static final String HEADER_OPEN_TAG = "<h";
    private static final String HEADER_CLOSE_TAG = "</h";
    private static final String PARAGRAPH_OPEN_TAG = "<p>";
    private static final String PARAGRAPH_CLOSE_TAG = "</p>";
    private static final String LINK_OPEN_TAG = "<a";
    private static final String LINK_CLOSE_TAG = "</a>";
    private static final String HREF_OPEN_TAG = " href=\"";
    private static final String HREF_CLOSE_TAG = "\">";

    /**
    * This method receives a line of text in Markdown format and converts it to a line of HTML.
    * @param String(line): a line of text from the given Markdown file 
    */
    public static String convertMarkdownLineToHTML(String line){

        boolean readingcurrentHeaderLevel = false, readingHeader = false, readingParagraph = false, readingLinkTextContent = false, readingHref = false;
        String linkTextContent = "", href="", htmlOutputLine = "";
        int currentHeaderLevel = 0;    

        //Read through each line character by character, taking into account any keys such as '#' and pairs of parenthesis.
        for (char character : line.toCharArray()) {
            switch (character) {
                case HEADER_OPEN:
                    //Nested headers are not valid - just add '#' if already inside of a header element
                    if(readingHeader){
                        htmlOutputLine += (character);
                        break;
                    }
                    readingcurrentHeaderLevel = true;
                    currentHeaderLevel++;
                    break;
                case LINK_OPEN:
                    htmlOutputLine += (LINK_OPEN_TAG);
                    readingLinkTextContent = true;
                    break;
                case LINK_CLOSE:
                    readingLinkTextContent = false;
                    break;
                case HREF_OPEN:
                    readingHref = true;                            
                    break;
                case HREF_CLOSE:
                    if (readingHref) {
                        htmlOutputLine += (HREF_OPEN_TAG + href + HREF_CLOSE_TAG + linkTextContent + LINK_CLOSE_TAG);
                        readingHref = false;
                    }
                    break;
                //The default case is executed if the character is not one of the keys '#', '()', or '[].'
                default:
                    if (readingLinkTextContent) {
                        linkTextContent += character;
                    } 
                    else if(readingHref){
                        href += character;
                    }
                    else if (readingcurrentHeaderLevel && character == EMPTY_SPACE) {
                        htmlOutputLine += (HEADER_OPEN_TAG + currentHeaderLevel + ">");
                        readingcurrentHeaderLevel = false;
                        readingHeader = true;
                    } 
                    else if (!readingHeader && !readingParagraph) {
                        htmlOutputLine += (PARAGRAPH_OPEN_TAG+character);
                        readingParagraph = true;
                    }
                    else {
                        htmlOutputLine += (character);
                    }
                    break;
            }
        }
        //Ensure to close the end of a header or paragraph with the corresponding tag.
        if (readingHeader) {
            htmlOutputLine += (HEADER_CLOSE_TAG + currentHeaderLevel + ">");
        } 
        if(readingParagraph) {
            htmlOutputLine += (PARAGRAPH_CLOSE_TAG);
        }
        htmlOutputLine += ("\n");
        return htmlOutputLine;
    }
    /**
    * The main method handles all input/output, including interaction with the user via the command line
    * as well as reading/writing from/to files. 
    *
    * In addition, it calls the 'convertMarkdownLineToHTML' method with a String read from the input file
    * and writes the returned String into the output file. 
    */
    public static void main(String[] args) throws IOException, FileNotFoundException{
        int numRuns = 0;
        String inputFileName = "";
        BufferedReader markdownInputFile = null;
        BufferedWriter htmlOutputFile = null;
        Scanner scanner = new Scanner(System.in);  // Create a Scanner object
        System.out.println("\nWelcome to my Markdown to HTML Converter! Enter 'Q' at any time to exit.");

            while(!(inputFileName.equals("Q"))){
                try{
                    System.out.print("\nEnter a markdown filename to convert to HTML (extension must be '.md'): ");
                    inputFileName = scanner.next(); 
                    if(inputFileName.equals("Q")){
                        break;
                    }
                    else if(!isUserInputValid(inputFileName)){
                        continue;
                    }
                    String outputFileName = "myHTML", line = "";
                    if(numRuns > 0){
                        outputFileName += Integer.toString(numRuns);
                    }
                    markdownInputFile = new BufferedReader(new FileReader(inputFileName));
                    htmlOutputFile = new BufferedWriter(new FileWriter(outputFileName += ".html"));
                    //Process each line in the file one-by-one, ensuring to handle exceptions
                    while ((line = markdownInputFile.readLine()) != null) {
                        if(line.length()!=0){
                            htmlOutputFile.write(convertMarkdownLineToHTML(line));
                        }
                    }
                    System.out.println("Success! View your HTML file in " + outputFileName);
                    markdownInputFile.close();
                    htmlOutputFile.close();
                    numRuns++;
                }catch(FileNotFoundException fileNotFound){
                    System.out.println("\nA file with the given name was not found in this directory.\n");
                }catch(IOException e){
                    System.out.println("\nAn error occurred - please try again.\n");
                }
            }
        scanner.close();
    }

    public static boolean isUserInputValid(String userInput){
        //If an empty filename/non-.md file is given, re-prompt user.
        if(userInput == null || userInput.length() == 0){
            System.out.println("Please input a file name.");
            return false;
        }else if(!((userInput.substring(userInput.length()-3))).equals(".md")){ 
            System.out.println("Please input a valid file with extension '.md.'");
            return false;
        }
        return true;
    }
}

