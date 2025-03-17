import java.io.IOException;

public class HackAssembler {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HackAssembler <input.asm>");
            System.exit(1);
        }

        String inputFile = args[0];

        Assembler assembler = new Assembler(inputFile, null); // The outputFile is set in the constructor
        try {
            assembler.assemble();
            System.out.println("Assembly complete. Output written to " + assembler.getOutputFile());
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}