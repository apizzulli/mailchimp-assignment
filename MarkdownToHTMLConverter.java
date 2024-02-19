import java.io.*;

/*
* This class is a Markdown to HTML file converter. The user will input a Markdown file and receive   
* an HTML file as output, converted according to the specifications described in 'README.md.'
*
* Run 'java MarkdownProcessor.java' in the command line in the same directory as this file to start.
*
* Author: Anthony Pizzulli
*/

public class MarkdownToHTMLConverter{
    
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
    private static final String FILE_FORMAT_ERROR_MESSAGE = "Invalid input file format: ";

    /*
    * This method receives a line of text (String) in Markdown format and returns the equivalent HTML String
    */
    public static String convertMarkdownLineToHTML(String line) throws InvalidFileFormatException{

        boolean readingCurrentHeaderLevel = false, readingHeader = false, readingParagraph = false, readingLinkTextContent = false, readingHref = false;
        String linkTextContent = "", href="", htmlOutputLine = "";
        int currentHeaderLevel = 0;    

        //Read through each line character by character, taking into caccount any keys such as '#' and pairs of parenthesis.
        for (char character : line.toCharArray()) {
            switch (character) {
                case HEADER_OPEN:
                    //Just add '#' if already inside of a another element
                    if(readingHeader || readingParagraph || readingHref || readingLinkTextContent){
                        htmlOutputLine += character;
                        break;
                    }
                    readingCurrentHeaderLevel = true;
                    currentHeaderLevel++;
                    break;
                case LINK_OPEN:
                    readingLinkTextContent = true;
                    break;
                case LINK_CLOSE:
                    readingLinkTextContent = false;
                    break;
                case HREF_OPEN:
                    //If reading an HREF and the link text has not been closed, the format is invalid
                    if(readingLinkTextContent){
                        throw new InvalidFileFormatException(FILE_FORMAT_ERROR_MESSAGE + "'(' missing closing ')'");
                    }
                    readingHref = true;                            
                    break;
                case HREF_CLOSE:
                    if (readingHref) {
                        htmlOutputLine += LINK_OPEN_TAG + HREF_OPEN_TAG + href + HREF_CLOSE_TAG + linkTextContent + LINK_CLOSE_TAG;
                        readingHref = false;
                    }
                    break;
                //The default case is executed if the character is not one of the keys '#', '()', or '[]'
                default:
                    if (readingLinkTextContent) {
                        linkTextContent += character;
                    } 
                    else if(readingHref){
                        href += character;
                    }
                    else if (readingCurrentHeaderLevel && character == EMPTY_SPACE) {
                        htmlOutputLine += HEADER_OPEN_TAG + currentHeaderLevel + ">";
                        readingCurrentHeaderLevel = false;
                        readingHeader = true;
                    } 
                    else if (!readingHeader && !readingParagraph) {
                        htmlOutputLine += PARAGRAPH_OPEN_TAG+character;
                        readingParagraph = true;
                    }
                    else {
                        htmlOutputLine += (character);
                    }
                    break;
            }
        }
        //Close the end of a header or paragraph with the corresponding tag
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
    * as well as reading/writing from/to files, and handles all exceptions.
    *
    * In addition, it calls the 'convertMarkdownLineToHTML' method with a String read from the input file
    * and writes the returned String into the output file. 
    */
    public static void main(String[] args) throws IOException, FileNotFoundException{
        int numRuns = 0;
        String inputFileName = "", htmlOutputText = "", outputFileName = "", line = " ";
        BufferedReader markdownInputFile = null;
        BufferedWriter htmlOutputFile = null;
        //Scanner scanner = new Scanner(System.in);  
        BufferedReader scanner = new BufferedReader (new InputStreamReader (System.in));

        System.out.println("\nWelcome to my Markdown to HTML Converter! Enter 'Q' at any time to exit.");

            while(!(inputFileName.equals("Q"))){
                try{
                    System.out.print("\nEnter a markdown file name to convert to HTML (extension must be '.md'): ");
                    inputFileName = scanner.readLine(); 
                    if(inputFileName.equals("Q")){
                        break;
                    }
                    else if(!isUserInputValid(inputFileName)){
                        continue;
                    }
                    System.out.print("Enter a name for the output file (if none given, defaults to 'myhtml.html'): ");
                    outputFileName = scanner.readLine(); 
                    if(outputFileName.isEmpty()){
                        outputFileName = "myHTML";
                        if(numRuns > 0)
                            outputFileName += Integer.toString(numRuns);
                    }
                    markdownInputFile = new BufferedReader(new FileReader(inputFileName));
                    //Process each line in the file one-by-one, building the String that will be written to the output file
                    while ((line = markdownInputFile.readLine()) != null) {
                        if(line.length()!=0){
                            htmlOutputText += convertMarkdownLineToHTML(line);
                        }
                    }
                    htmlOutputFile = new BufferedWriter(new FileWriter(outputFileName += ".html"));
                    htmlOutputFile.write(htmlOutputText);
                    System.out.println("\nSuccess! View your HTML file in " + outputFileName);
                    htmlOutputFile.close();
                    numRuns++;
                }catch(FileNotFoundException fileNotFound){
                    System.out.println("\nA file with the given name was not found in this directory.\n");
                }catch(IOException e){
                    System.out.println(e.getMessage());
                }catch(InvalidFileFormatException invalidFileFormatException){
                    System.out.println(invalidFileFormatException.getMessage());
                }
                markdownInputFile.close();
            }
        scanner.close();
    }

    public static boolean isUserInputValid(String userInput){
        //If an empty file name/non-.md file is given, re-prompt user
        if(userInput == null || userInput.length() == 0 || userInput.length() < 4){
            System.out.println("Please input a valid file name.");
            return false;
        }else if(!((userInput.substring(userInput.length()-3))).equals(".md")){ 
            System.out.println("Please input a file name with extension '.md'");
            return false;
        }
        return true;
    }
}

class InvalidFileFormatException extends Exception { 
    public InvalidFileFormatException(String errorMessage) {
        super(errorMessage);
    }
}