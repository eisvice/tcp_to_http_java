import java.io.ByteArrayInputStream;

public class ChunkReader extends ByteArrayInputStream {
    private String data;
    private int numBytesPerRead;

    public ChunkReader(String data, int numBytesPerRead) {
        super(data.getBytes());
        this.data = data;
        this.numBytesPerRead = numBytesPerRead;
    }

    public int read(byte[] p) {
        int bytesToRead = this.numBytesPerRead;
        if (this.pos + bytesToRead > this.buf.length) {
            bytesToRead = this.buf.length - this.pos;
        }

        return this.read(p, this.pos, bytesToRead);
    }

    public String getData() {
        return data;
    }

    public int getNumBytesPerRead() {
        return numBytesPerRead;
    }
}