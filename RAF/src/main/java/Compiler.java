import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Compiler {
    public static void main(String[] args) throws IOException {
        RandomAccessFile f = new RandomAccessFile("С:/Workspace/asm/3PR.obj", "r");
        byte[] header = new byte[20];
        f.read(header);
        int ns = frun16(header[2], header[3]);
        System.out.println(ns);
        int SymbolOffSet = frun32(header[8], header[9], header[10], header[11]);
        System.out.println(SymbolOffSet);
        int nSymbols = frun32(header[12], header[13], header[14], header[15]);
        System.out.println(nSymbols);
        int optHeader = frun16(header[16], header[17]);
        System.out.println(optHeader);

        int stringTablePosition = SymbolOffSet + 18 * nSymbols;
        f.seek(stringTablePosition);
        byte[] length = new byte[4];
        f.read(length);
        int stringTableLength = frun32(length[0], length[1], length[2], length[3]);
        f.seek(stringTablePosition);
        byte[] stringTable = new byte[stringTableLength];
        f.read(stringTable);

        f.seek(SymbolOffSet);
        for (int i = 0; i < nSymbols;){
            byte[] sym = new byte[18];
            f.read(sym);
            String info =  getName(sym, stringTable);
            System.out.println(info);
            int skip = (sym[17] & 0xFF);
            if (skip > 0) {
                i += skip + 1;
                f.skipBytes(18 * skip);
            } else {
                i++;
            }

        }

        System.out.println();
        f.seek(20 + optHeader);
        Map<String, int[]> sections = new HashMap<>();
        for (int i = 0; i < ns; i++){
            byte[] data = new byte[40];
            f.read(data);
            String nameSection = new String(data, 0, 8, StandardCharsets.US_ASCII);
            int lengthSection = frun32(data[16], data[17], data[18], data[19]);
            int ofsSection = frun32(data[20], data[21], data[22], data[23]);
            sections.put(nameSection, new int[] {lengthSection, ofsSection});
        }
        for (Map.Entry<String, int[]> stringEntry : sections.entrySet()) {
            String Name = stringEntry.getKey();
            int[] infos = stringEntry.getValue();
            System.out.println(Name + " " + Arrays.toString(infos));
            f.seek(infos[1]);
            byte[] data = new byte[infos[0]];
            f.read(data);
            System.out.println(new BigInteger(data).toString(16));
        }
    }


    public static String getName(byte[] data, byte[] stringTable) throws UnsupportedEncodingException {
        int
                name = frun32(data[0], data[1], data[2], data[3]);
        int lengthSymbol = frun16(data[12], data[13]);
        if (name == 0) {
            int sofs = frun32(data[4], data[5], data[6], data[7]);
            int length = 0;
            for (int i = sofs; i < stringTable.length; i++) {
                if (stringTable[i] == 0) {
                    break;
                }
                length ++;
            }
            return new String(stringTable, sofs, length, StandardCharsets.US_ASCII) + " section: " + lengthSymbol;
        }
        return new String(data, 0, 8, StandardCharsets.US_ASCII) + " section: " + lengthSymbol;

    }

    public static byte frun16(byte b0, byte b1){
        return (byte) ((b1 & 0xFF) << 8 | b0 & 0xFF);
    }
    public static int frun32(byte b0, byte b1, byte b2, byte b3){
        int i = b3 & 0xFF;
        i = (i << 8) | (b2 & 0xFF);
        i = (i << 8) | (b1 & 0xFF);
        i = (i << 8) | (b0 & 0xFF);
        return i;
    }
}
