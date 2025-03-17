import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private List<String> lines;
    private int currentLine;
    private String currentCommand;


    public Parser(String filename) throws IOException {
        lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Remove comments
                int commentIndex = line.indexOf("//");
                if (commentIndex != -1) {
                    line = line.substring(0, commentIndex);
                }

                // Remove whitespace
                line = line.trim();

                // Skip empty lines
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        }

        currentLine = 0;
        currentCommand = null;
    }


    public boolean hasMoreCommands() {
        return currentLine < lines.size();
    }


    public void advance() {
        if (hasMoreCommands()) {
            currentCommand = lines.get(currentLine);
            currentLine++;
        }
    }


    public CommandType commandType() {
        if (currentCommand.startsWith("@")) {
            return CommandType.A_COMMAND;
        } else if (currentCommand.startsWith("(") && currentCommand.endsWith(")")) {
            return CommandType.L_COMMAND;
        } else {
            return CommandType.C_COMMAND;
        }
    }


    public String symbol() {
        if (commandType() == CommandType.A_COMMAND) {
            return currentCommand.substring(1); // Remove @ from @symbol
        } else if (commandType() == CommandType.L_COMMAND) {
            return currentCommand.substring(1, currentCommand.length() - 1); // Remove ( and ) from (symbol)
        } else {
            return null;
        }
    }


    public String dest() {
        if (commandType() != CommandType.C_COMMAND) {
            return null;
        }

        if (currentCommand.contains("=")) {
            return currentCommand.split("=")[0];
        } else {
            return "";
        }
    }


    public String comp() {
        if (commandType() != CommandType.C_COMMAND) {
            return null;
        }

        String command = currentCommand;

        if (command.contains("=")) {
            command = command.split("=")[1];
        }

        if (command.contains(";")) {
            command = command.split(";")[0];
        }

        return command;
    }


    public String jump() {
        if (commandType() != CommandType.C_COMMAND) {
            return null;
        }

        if (currentCommand.contains(";")) {
            return currentCommand.split(";")[1];
        } else {
            return "";
        }
    }
}