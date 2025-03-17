public enum CommandType {
    A_COMMAND,  // @xxx where xxx is either a symbol or a decimal number
    C_COMMAND,  // dest=comp;jump
    L_COMMAND   // (xxx) where xxx is a symbol
}