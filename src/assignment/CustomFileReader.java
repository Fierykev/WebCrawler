package assignment;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class CustomFileReader extends Reader {

    private static final int CHUNK = 4000;
    private RandomAccessFile file;
    private FileChannel channel;
    private ByteBuffer byteBuf;
    private int numBytes = 0, remBytes = 0;

    public CustomFileReader(String filename) throws FileNotFoundException {
        file = new RandomAccessFile(filename, "r");
        channel = file.getChannel();
        byteBuf = ByteBuffer.allocate(CHUNK);
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
        channel.close();
        file.close();
    }

    private void bufferRead() throws IOException {
        byteBuf.clear();

        numBytes = channel.read(byteBuf);

        remBytes = numBytes;
        
        byteBuf.flip();
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {

        // read more in if no more bytes are available
        if (remBytes == 0) {
            bufferRead();
        }

        // end of file found
        if (remBytes == -1)
            return -1;

        // convert the bytes into the output array
        int numRead;
        for (numRead = 0; numRead < len && 0 < remBytes; numRead++) {

            // transfer to the buffer
            cbuf[numRead + off] = (char)(byteBuf.get() & 0xFF);

            // remove one from the remaining bytes
            remBytes--;
        }
//        System.out.println(numRead + " " + len + " " + remBytes);
        return numRead;
    }

}
