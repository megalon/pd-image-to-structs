# Convert an image file to Pure Data structs!

After making my last tutorial I was wondering: "Would it be possible to display an image within the regular pure data patch window using only structs?" 

It turns out, not only is it possible, it actually works pretty well!

## But... How?

Each pixel within the input image file is drawn as a square using the [drawpolygon] object. If we set the squares to be 1x1 pixel, we are essentially rendering the image onto the PD canvas!
I wrote an external tool in Java that allows you to easily create a custom abstraction whose sole job is to display whatever image you convert.
