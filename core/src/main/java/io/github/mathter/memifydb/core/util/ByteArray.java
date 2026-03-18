package io.github.mathter.memifydb.core.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;

public class ByteArray {
    private static final VarHandle SHORT = create(short[].class);

    private static final VarHandle CHAR = create(char[].class);

    private static final VarHandle INT = create(int[].class);

    private static final VarHandle LONG = create(long[].class);

    private static final VarHandle FLOAT = create(float[].class);

    private static final VarHandle DOUBLE = create(double[].class);

    private static VarHandle create(Class<?> viewArrayClass) {
        return MethodHandles.byteArrayViewVarHandle(viewArrayClass, ByteOrder.BIG_ENDIAN);
    }

    public static byte getByteRaw(byte[] buf, int offset) {
        return buf[offset];
    }

    public static void setByteRaw(byte[] buf, int offset, byte value) {
        buf[offset] = value;
    }

    public static short getShortRaw(byte[] buf, int offset) {
        return (short) SHORT.get(buf, offset);
    }

    public static void setShortRaw(byte[] buf, int offset, short value) {
        SHORT.set(buf, offset, value);
    }

    public static char getCharRaw(byte[] buf, int offset) {
        return (char) CHAR.get(buf, offset);
    }

    public static void setCharRaw(byte[] buf, int offset, char value) {
        CHAR.set(buf, offset, value);
    }

    public static int getIntRaw(byte[] buf, int offset) {
        return (int) INT.get(buf, offset);
    }

    public static void setIntRaw(byte[] buf, int offset, int value) {
        INT.set(buf, offset, value);
    }

    public static long getLongRaw(byte[] buf, int offset) {
        return (long) LONG.get(buf, offset);
    }

    public static void setLongRaw(byte[] buf, int offset, long value) {
        LONG.set(buf, offset, value);
    }

    public static float getFloat(byte[] buf, int offset) {
        return (float) Float.intBitsToFloat((int) INT.get(buf, offset));
    }

    public static void setFloat(byte[] buf, int offset, float value) {
        INT.set(buf, offset, Float.floatToIntBits(value));
    }

    public static float getFloatRaw(byte[] buf, int offset) {
        return (float) FLOAT.get(buf, offset);
    }

    public static void setFloatRaw(byte[] buf, int offset, float value) {
        FLOAT.set(buf, offset, value);
    }

    public static double getDouble(byte[] buf, int offset) {
        return Double.longBitsToDouble((long) LONG.get(buf, offset));
    }

    public static void setDouble(byte[] buf, int offset, double value) {
        LONG.set(buf, offset, Double.doubleToLongBits(value));
    }

    public static double getDoubleRaw(byte[] buf, int offset) {
        return (double) DOUBLE.get(buf, offset);
    }

    public static void setDoubleRaw(byte[] buf, int offset, double value) {
        DOUBLE.set(buf, offset, value);
    }

    public static byte readByteRaw(InputStream is) throws IOException {
        int result = is.read();
        if (result == -1) {
            throw new EOFException("Unexpected end of stream");
        }
        return (byte) result;
    }

    public static void writeByteRaw(OutputStream os, byte value) throws IOException {
        os.write(value);
    }

    public static short readShortRaw(InputStream is) throws IOException {
        final byte[] buf = new byte[Short.BYTES];
        final int read = is.readNBytes(buf, 0, buf.length);
        if (read != buf.length) {
            throw new EOFException("Expected " + buf.length + " bytes, got " + read);
        }
        return (short) SHORT.get(buf, 0);
    }

    public static void writeShortRaw(OutputStream os, short value) throws IOException {
        final byte[] buf = new byte[Short.BYTES];
        SHORT.set(buf, 0, value);

        os.write(buf);
    }

    public static char readCharRaw(InputStream is) throws IOException {
        final byte[] buf = new byte[Character.BYTES];
        final int read = is.readNBytes(buf, 0, buf.length);
        if (read != buf.length) {
            throw new EOFException("Expected " + buf.length + " bytes, got " + read);
        }
        return (char) CHAR.get(buf, 0);
    }

    public static void writeCharRaw(OutputStream os, char value) throws IOException {
        final byte[] buf = new byte[Character.BYTES];
        CHAR.set(buf, 0, value);

        os.write(buf);
    }

    public static int readIntRaw(InputStream is) throws IOException {
        final byte[] buf = new byte[Integer.BYTES];
        final int read = is.readNBytes(buf, 0, buf.length);
        if (read != buf.length) {
            throw new EOFException("Expected " + buf.length + " bytes, got " + read);
        }
        return (int) INT.get(buf, 0);
    }

    public static void writeIntRaw(OutputStream os, int value) throws IOException {
        final byte[] buf = new byte[Integer.BYTES];
        INT.set(buf, 0, value);

        os.write(buf);
    }

    public static long readLongRaw(InputStream is) throws IOException {
        final byte[] buf = new byte[Long.BYTES];
        final int read = is.readNBytes(buf, 0, buf.length);
        if (read != buf.length) {
            throw new EOFException("Expected " + buf.length + " bytes, got " + read);
        }
        return (long) LONG.get(buf, 0);
    }

    public static void writeLongRaw(OutputStream os, long value) throws IOException {
        final byte[] buf = new byte[Long.BYTES];
        LONG.set(buf, 0, value);

        os.write(buf);
    }

    public static float readFloat(InputStream is) throws IOException {
        final byte[] buf = new byte[Integer.BYTES];
        final int read = is.readNBytes(buf, 0, buf.length);
        if (read != buf.length) {
            throw new EOFException("Expected " + buf.length + " bytes, got " + read);
        }
        return Float.intBitsToFloat((int) INT.get(buf, 0));
    }

    public static void writeFloat(OutputStream os, float value) throws IOException {
        final byte[] buf = new byte[Integer.BYTES];
        INT.set(buf, 0, Float.floatToRawIntBits(value));

        os.write(buf);
    }

    public static float readFloatRaw(InputStream is) throws IOException {
        final byte[] buf = new byte[Float.BYTES];
        final int read = is.readNBytes(buf, 0, buf.length);
        if (read != buf.length) {
            throw new EOFException("Expected " + buf.length + " bytes, got " + read);
        }
        return (float) FLOAT.get(buf, 0);
    }

    public static void writeFloatRaw(OutputStream os, float value) throws IOException {
        final byte[] buf = new byte[Float.BYTES];
        FLOAT.set(buf, 0, value);

        os.write(buf);
    }

    public static double readDouble(InputStream is) throws IOException {
        final byte[] buf = new byte[Long.BYTES];
        final int read = is.readNBytes(buf, 0, buf.length);
        if (read != buf.length) {
            throw new EOFException("Expected " + buf.length + " bytes, got " + read);
        }
        return Double.longBitsToDouble((long) LONG.get(buf, 0));
    }

    public static void writeDouble(OutputStream os, double value) throws IOException {
        final byte[] buf = new byte[Long.BYTES];
        LONG.set(buf, 0, Double.doubleToLongBits(value));

        os.write(buf);
    }

    public static double readDoubleRaw(InputStream is) throws IOException {
        final byte[] buf = new byte[Double.BYTES];
        final int read = is.readNBytes(buf, 0, buf.length);
        if (read != buf.length) {
            throw new EOFException("Expected " + buf.length + " bytes, got " + read);
        }
        return (double) DOUBLE.get(buf, 0);
    }

    public static void writeDoubleRaw(OutputStream os, double value) throws IOException {
        final byte[] buf = new byte[Double.BYTES];
        DOUBLE.set(buf, 0, value);

        os.write(buf);
    }
}
