import java.io.*;

//import gpdraw.*;

public class BitmapUtil { // za 1 pixel sam ne dela!!!! popravi...

	// Identity the file
	private short header;
	private int size;
	private short reserved1;
	private short reserved2;
	private int offset;
	// Picture
	private int headerSize;
	private int bitmapWidth;
	private int bitmapHeight;
	private short numColorPlanes;
	private short numBitsPixel;
	private int method;
	private int imageSize;
	private int horRes;
	private int vertRes;
	private int numColorPalette;
	private int numImportantColors;

	short[][] color;

	public short[][] loadBMP(String fileName) {
		try {
			FileInputStream f = new FileInputStream(fileName);
			DataInputStream d = new DataInputStream(f);
			// Identify the File
			header = Short.reverseBytes(d.readShort());
			size = Integer.reverseBytes(d.readInt());
			reserved1 = Short.reverseBytes(d.readShort());
			reserved2 = Short.reverseBytes(d.readShort());
			offset = Integer.reverseBytes(d.readInt());
			// Picture
			headerSize = Integer.reverseBytes(d.readInt());
			bitmapWidth = Integer.reverseBytes(d.readInt());
			bitmapHeight = Integer.reverseBytes(d.readInt());
			numColorPlanes = Short.reverseBytes(d.readShort());
			numBitsPixel = Short.reverseBytes(d.readShort());
			method = Integer.reverseBytes(d.readInt());
			imageSize = Integer.reverseBytes(d.readInt());
			horRes = Integer.reverseBytes(d.readInt());
			vertRes = Integer.reverseBytes(d.readInt());
			numColorPalette = Integer.reverseBytes(d.readInt());
			numImportantColors = Integer.reverseBytes(d.readInt());

			// Read pixels
			color = new short[bitmapHeight][bitmapWidth];
			for (int i = bitmapHeight - 1; i >= 0; i--) {
				int cnt = 0;
				int nextInt = Integer.reverseBytes(d.readInt());
				for (int j = 0; j < bitmapWidth; j++) {
					short tempShort = (short) (nextInt >>> (8 * cnt));
					if (tempShort < 0)
						tempShort += 256;
					tempShort = (short) (tempShort & 0xff);

					color[i][j] = tempShort;
					cnt = (cnt + 3) % 4;
					if ((cnt != 3 && j != bitmapWidth - 1)
							|| (cnt == 2 && j == bitmapWidth - 1))
						nextInt = Integer.reverseBytes(d.readInt());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return color;
	}

	public void printBMPHeader() {
		System.out.println("Header Field: " + header);
		System.out.println("Size of BMP File: " + size);
		System.out.println("Reserved(First): " + reserved1);
		System.out.println("Reserved(Second): " + reserved2);
		System.out.println("Offset: " + offset);
		System.out.println("Size of the header: " + headerSize);
		System.out.println("Bitmap width: " + bitmapWidth);
		System.out.println("Bitmap height: " + bitmapHeight);
		System.out.println("Number Of Color Planes: " + numColorPlanes);
		System.out.println("Number of bits per pixel: " + numBitsPixel);
		System.out.println("Compression Method: " + method);
		System.out.println("Image Size: " + imageSize);
		System.out.println("Horizontal Resolution of Image: " + horRes);
		System.out.println("Vertical Resolution of Image: " + vertRes);
		System.out.println("Number of colors in Color Palette: "
				+ numColorPalette);
		System.out.println("Number of Important Colors used: "
				+ numImportantColors);

		for (int i = 0; i < bitmapWidth; i++) {
			for (int j = 0; j < bitmapHeight; j++) {
				System.out.print(color[i][j] + " ");
			}
			System.out.println();
		}
	}

	public int getBitmapWidth() {
		return bitmapWidth;
	}

	public int getBitmapHeight() {
		return bitmapHeight;
	}

}