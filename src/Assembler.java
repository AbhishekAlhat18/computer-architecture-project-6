import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Assembler {
    private String inputFile;
    private String outputFile;
    private SymbolTable symbolTable;
    private Code code;


    public Assembler(String inputFile, String outputFile) {
        this.inputFile = inputFile;

        // Extract just the filename from the input path
        String fileName = new File(inputFile).getName();
        // Replace the extension and prepend the hack_files directory
        this.outputFile = "hack_files/" + fileName.replace(".asm", ".hack");

        this.symbolTable = new SymbolTable();
        this.code = new Code();
    }

    public String getOutputFile() {
        return outputFile;
    }


    public void assemble() throws IOException {
        // First pass: resolve labels
        firstPass();

        // Second pass: generate binary code
        secondPass();
    }


    private void firstPass() throws IOException {
        Parser parser = new Parser(inputFile);
        int romAddress = 0;

        while (parser.hasMoreCommands()) {
            parser.advance();

            if (parser.commandType() == CommandType.L_COMMAND) {
                // Add label to symbol table
                symbolTable.addEntry(parser.symbol(), romAddress);
            } else if (parser.commandType() == CommandType.A_COMMAND ||
                    parser.commandType() == CommandType.C_COMMAND) {
                // Only increment ROM address for A and C commands
                romAddress++;
            }
        }
    }


    private void secondPass() throws IOException {
        Parser parser = new Parser(inputFile);
        List<String> output = new ArrayList<>();

        while (parser.hasMoreCommands()) {
            parser.advance();

            if (parser.commandType() == CommandType.A_COMMAND) {
                // Handle A-instruction
                String symbol = parser.symbol();
                int address;

                try {
                    // Try to parse as a number
                    address = Integer.parseInt(symbol);
                } catch (NumberFormatException e) {
                    // It's a symbol, look it up or add it
                    if (!symbolTable.contains(symbol)) {
                        symbolTable.addEntry(symbol, symbolTable.getNextAddress());
                    }
                    address = symbolTable.getAddress(symbol);
                }

                // Convert to 16-bit binary
                String binary = String.format("%16s", Integer.toBinaryString(address)).replace(' ', '0');
                output.add(binary);

            } else if (parser.commandType() == CommandType.C_COMMAND) {
                // Handle C-instruction
                String compBits = code.comp(parser.comp());
                String destBits = code.dest(parser.dest());
                String jumpBits = code.jump(parser.jump());

                // Assemble the instruction: 1 + comp + dest + jump
                String binary = "111" + compBits + destBits + jumpBits;
                output.add(binary);
            }
        }

        // Write output to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (String line : output) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}