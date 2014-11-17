ContentAwareImageResizing
=========================
This program provides a tool for resizing images while preserving the content of the images.
This is implemented using a linked list where each node is connected to the left, right, up, down and by using a Dynaminc Programming approach:

1)	The pixels of the picture are turned into a linked list where each node is connected to the left, right, up, and down.

2)	The change in colour across each pixel is calculated. (vertical and horizontal)

3)	A minimum path either vertically or horizontally is found using dynamic programming.

4)	The minimum path is extracted and the links of the neighbours are updated.

5)	The remaining linked list is turned back into a picture.
