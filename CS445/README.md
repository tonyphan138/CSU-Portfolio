# Final Project for CS445 - Introduction to Machine Learning

## Brain Tumor Classification

In this project we explored two datasets that contained MRI scans of brains with tumors and brains without tumors. With these images, we loaded them into Numpy Arrays
and performed preprocessing and normalization of the data.

Once the data was normalized and split into training, testing, and validation sets, we created a Convolutional Neural Network model using TensorFlow.
This Model focused on two features; accuracy and minimizing false negatives. False negatives in this model indicated the MRI scans that were classified as
not having a brain tumor when in truth there was a tumor. Given that this model would be applied to the medical field, it would be best to minimize the 
false negative rate over the false positive rates.

## Results

With our model, we were able to achieve ~95-98% accuracy on average but was unable to minimize the false negative rates as much as we hoped. With this, 
however, we found that many of the misclassified images were due to the model not knowing exactly what features to focus on. If more images were provided, 
we predict that we would be able to achieve higher accuracy and apply it to real world MRI scans. 
