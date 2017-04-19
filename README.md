# Convert an image file to Pure Data structs!

![](https://raw.githubusercontent.com/megalon/pd-image-to-structs/master/images/image-to-structs-demo1.gif)

After making my last tutorial I was wondering: "Would it be possible to display an image within the regular pure data patch window using only structs?" 

It turns out, not only is it possible, it actually works pretty well!

## But... How?

Each pixel within the input image file is drawn as a square using the [drawpolygon] object. If we set the squares to be 1x1 pixel, we are essentially rendering the image onto the PD canvas!

![](https://github.com/megalon/pd-image-to-structs/blob/master/images/example-abstraction.PNG?raw=true)

I wrote an external tool in Java that allows you to easily create a custom abstraction whose sole job is to display whatever image you convert.

![](https://github.com/megalon/pd-image-to-structs/blob/master/images/example-gui.PNG?raw=true)

pd files are saved as plain text, so all I had to do was create a pd patch that would draw a square using [filledpolygon], then figure out where in the pd file that filledpolygon object was.
```
// PD patch to draw a square using filledpolygon
#N struct 1003-template float x float y;
#N canvas 623 177 1178 745 12;
#X msg 853 385 traverse \$1 \, bang;
#X msg 853 273 clear;
#X obj 680 203 t b b b;
#X msg 680 178 1;
#X obj 853 332 bng 15 250 50 0 empty empty empty 17 7 0 10 -262144 -1 -1;
#X obj 853 410 pointer;
#N canvas 1266 536 546 174 \$0-template 0;
#X obj 0 0 filledpolygon 888 888 0 0 0 1 0 1 1 0 1 0 0;

// We inject our filledpolygon objects here

#X obj 18 18 struct \$0-template float x float y;
#X restore 850 217 pd \$0-template;
#X obj 853 303 s pd-\$0-data;
#X obj 853 360 symbol pd-\$0-data;
#N canvas 147 576 450 300 \$0-data 0;
#X scalar 1003-template 0 0 \;;
#X coords 0 0 15 15 15 15 2 0 0;
#X restore 0 0 pd \$0-data;
#X obj 680 438 append \$0-template x y;
#X obj 680 228 t b b;
#X msg 680 392 0;
#X msg 717 392 0;
#X obj 680 109 inlet on-off;
#X obj 680 134 sel 1;
#X connect 0 0 5 0;
#X connect 1 0 7 0;
#X connect 2 0 11 0;
#X connect 2 1 4 0;
#X connect 2 2 1 0;
#X connect 3 0 2 0;
#X connect 4 0 8 0;
#X connect 5 0 10 2;
#X connect 8 0 0 0;
#X connect 11 0 12 0;
#X connect 11 1 13 0;
#X connect 12 0 10 0;
#X connect 13 0 10 1;
#X connect 14 0 15 0;
#X connect 15 0 3 0;
#X connect 15 1 1 0;
#X coords 0 0 1 1 23 47 2 0 0;
#X coords 0 0 1 1 160 160 2 0 0;
```
Since draw objects don't need to be connected to anything, we can just insert as many objects as we want without messing up the rest of the patch.

Assuming that we have already loaded the image in Java, here's the code to convert the image data into [filledpolygon] objects.

```Java
// Loop through all of the pixels in our image.
for(int x = 0; x < w; ++x){
  for(int y = 0; y < h; ++y){

    int rgb = img.getRGB(x, y);

    // Get individual R G B values. 
    // Convert integer RGB to a value between 0 and 9  
    // They are typically 0 - 255, so we convert to 0 - 9
    int red = ((rgb & 0xFF) * 9) / 256;
    int green = (((rgb >> 8) & 0xFF) * 9) / 256;
    int blue = (((rgb >> 16) & 0xFF) * 9) / 256;

    // With PD structs the color needs to be a 3 digit value. 
    // For example, 100% red would be:
    // 		R G B 
    // 		9 0 0
    rgb = red + green * 10 + blue * 100;

    writer.println("#X obj " + x*tilesize + " " + y*tilesize + " filledpolygon " + rgb + " " + rgb + " " + 0 + " "
          + x*tilesize + " "
          + y*tilesize + " "
          + (x*tilesize + tilesize) + " "
          + y*tilesize + " "
          + (x*tilesize + tilesize) + " "
          + (y*tilesize + tilesize) + " "
          + x*tilesize + " "
          + (y*tilesize + tilesize) + " "
          + x*tilesize + " "
          + y*tilesize
          + ";");
  }
}
```

# Limitations?
**Any image bigger than about 100 x 100px starts to really lag the GUI.** 
Try and position the image where you want it before you enable it. If you want to edit anything within the GUI, it'd be best to disable the image, then reenable it when you're done.

**PD will almost always crash if you try and close the file if the image is still visible.**
Be sure to turn the image off before closing.

While this process isn't very practical for any reasonably sized images, it might be good for adding some pixel art to your PD patches!
