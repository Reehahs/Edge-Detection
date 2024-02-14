package edge_detection;



import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;


public class EdgeDetection {

//processes the rgb values if they are messy
//RGB values range form 0 to 255 so we must account for such incase 
//in our convolution they are larger or less than these.
    public static int truncate(double a) {
        if (a <   0.0) {
            return 0;
        }   
        else if (a > 255.0) {
            return 255;
        }
        else{
            return (int) a;
        }
    }

    public static double singlePixelConvolution(double[][] input,
                                                int x, int y,
                                                double[][] k,
                                                int kernelWidth,
                                                int kernelHeight) {
        double output = 0;
        for (int i = 0; i < kernelWidth; ++i) {
            for (int j = 0; j < kernelHeight; ++j) {
                output = output + (input[x + i][y + j] * k[i][j]);
            }
        }
        return output;
    }


    /**
     * Takes a 2D array of grey-levels and a kernel and applies the convolution
     * over the area of the image specified by width and height.
     *
     * @param input        the 2D double array representing the image
     * @param width        the width of the image
     * @param height       the height of the image
     * @param kernel       the 2D array representing the kernel
     * @param kernelWidth  the width of the kernel
     * @param kernelHeight the height of the kernel
     * @return the 2D array representing the new image
     */
    public static double[][] convolution2D(double[][] input,
                                           int width, int height,
                                           double[][] kernel,
                                           int kernelWidth,
                                           int kernelHeight) {
        int smallWidth = width - kernelWidth + 1;
        int smallHeight = height - kernelHeight + 1;
        double[][] output = new double[smallWidth][smallHeight];
        for (int i = 0; i < smallWidth; ++i) {
            for (int j = 0; j < smallHeight; ++j) {
                output[i][j] = 0;
            }
        }
        for (int i = 0; i < smallWidth; ++i) {
            for (int j = 0; j < smallHeight; ++j) {
                output[i][j] = singlePixelConvolution(input, i, j, kernel,
                        kernelWidth, kernelHeight);
            }
        }
        return output;
    }

    /**
     * Takes a 2D array of grey-levels and a kernel, applies the convolution
     * over the area of the image specified by width and height and returns
     * a part of the final image.
     *
     * @param input        the 2D double array representing the image
     * @param width        the width of the image
     * @param height       the height of the image
     * @param kernel       the 2D array representing the kernel
     * @param kernelWidth  the width of the kernel
     * @param kernelHeight the height of the kernel
     * @return the 2D array representing the new image
     */
    public static double[][] convolution2DPadded(double[][] input,
                                                 int width, int height,
                                                 double[][] kernel,
                                                 int kernelWidth,
                                                 int kernelHeight) {
        int smallWidth = width - kernelWidth + 1;
        int smallHeight = height - kernelHeight + 1;
        int top = kernelHeight / 2;
        int left = kernelWidth / 2;

        double[][] small = convolution2D(input, width, height,
                kernel, kernelWidth, kernelHeight);
        double large[][] = new double[width][height];
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                large[i][j] = 0;
            }
        }
        for (int j = 0; j < smallHeight; ++j) {
            for (int i = 0; i < smallWidth; ++i) {
                large[i + left][j + top] = small[i][j];
            }
        }
        return large;
    }


    /**
     * Applies the convolution2DPadded  algorithm to the input array as many as
     * iterations.
     *
     * @param input        the 2D double array representing the image
     * @param width        the width of the image
     * @param height       the height of the image
     * @param kernel       the 2D array representing the kernel
     * @param kernelWidth  the width of the kernel
     * @param kernelHeight the height of the kernel
     * @param iterations   the number of iterations to apply the convolution
     * @return the 2D array representing the new image
     */
    public static double[][] convolutionType2(double[][] input,
                                       int width, int height,
                                       double[][] kernel,
                                       int kernelWidth, int kernelHeight,
                                       int iterations) {
        double[][] newInput = input.clone();
        double[][] output = input.clone();

        for (int i = 0; i < iterations; ++i) {
            output = convolution2DPadded(newInput, width, height,
                    kernel, kernelWidth, kernelHeight);
            newInput = output.clone();
        }
        return output;
    }

    public static void main(String [] args) throws Exception {
        //vertical filter
        double[][] vertFilter = {
            { -1,  0,  1 },
            { -1,  0,  1 },
            { -1,  0,  1 }
        };
        //horizontal  filter
        // double[][] horFilter = {
        //     {  1,  1,  1 },
        //     {  0,  0,  0 },
        //     { -1, -1, -1 }
        // };

        
        
        //THIS IS THE INPUT FILE, CHANGE THIS TO THE DIRECTORY OF PICTURE THAT YOU WANT EDGE DETECTED
        BufferedImage image = ImageIO.read(new File("flowerTest.png"));
        int width = image.getWidth();
        int height = image.getHeight();


        //3d array to hold all teh colors of RGB
        // 0 - Red
        // 1 - Green
        // 2 - Blue
        double imageArr[][][] = new double[3][height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color color = new Color(image.getRGB(j, i));
                imageArr[0][i][j] = color.getRed();
                imageArr[1][i][j] = color.getGreen();
                imageArr[2][i][j] = color.getBlue();
            }
        }
        // Convolution is not my data structure and has been referenced in my report
        //this way of takeing a 3d array and applying convultions to the RGB pixels of the image was also inspirted by the same creator of the conlution function structure and has been
        //credited in my report.
        // Convolution convolution = new Convolution();
        double[][] redConv = convolutionType2(imageArr[0], height, width, vertFilter, 3, 3, 1);
        double[][] greenConv = convolutionType2(imageArr[1], height, width, vertFilter, 3, 3, 1);
        double[][] blueConv = convolutionType2(imageArr[2], height, width, vertFilter, 3, 3, 1);
        double[][] finalConv = new double[redConv.length][redConv[0].length];
        for (int i = 0; i < redConv.length; i++) {
            for (int j = 0; j < redConv[i].length; j++) {
                finalConv[i][j] = redConv[i][j] + greenConv[i][j] + blueConv[i][j];
            }
        }
        //final conv now hold the final convoluted pixels of the image
        // now convert the pixels back into an image
        //new image should be same as the original, still in rgb, 0 gives white, 255 would give black
        BufferedImage edgedImage = new BufferedImage(350,525,BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < finalConv.length; i++) {
            for (int j = 0; j < finalConv[i].length; j++) {
                Color color = new Color(truncate(finalConv[i][j]),
                        truncate(finalConv[i][j]),
                        truncate(finalConv[i][j]));
                edgedImage.setRGB(j, i, color.getRGB());
            }
        }
        
        //THIS IS THE OUTPUT FILE
        File outputFile = new File("edgesTmp.png");
        ImageIO.write(edgedImage, "png", outputFile);

    
    }






}