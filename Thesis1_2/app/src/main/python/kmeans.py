import numpy as np
import cv2
from sklearn.cluster import KMeans
from PIL import Image
import base64
import io
import warnings
warnings.filterwarnings("ignore")

def main(data):
    # dedcode the data
    decoded_data = base64.b64decode(data)
    np_data = np.fromstring(decoded_data, np.uint8)
    im = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)
    
    
    im = cv2.cvtColor(im,cv2.COLOR_BGR2RGB)

    original_shape = im.shape
    all_pixels  = im.reshape((-1,3))

    dominant_colors = 5
    km = KMeans(n_clusters=dominant_colors)
    km.fit(all_pixels)

    centers = km.cluster_centers_
    centers = np.array(centers,dtype='uint8')

    # Storing info in color list
    colors = list(centers)

    new_img = np.zeros((im.shape[0]*im.shape[1],3),dtype='uint8')
    unique_pixels = np.zeros(dominant_colors)

    for ix in range(new_img.shape[0]):
        unique_pixels[int(km.labels_[ix].item())]+=1   
        new_img[ix] = colors[km.labels_[ix].item()]

    new_img = new_img.reshape((original_shape))

    #calculate percentage
    hotspotPercentage = format((np.min(unique_pixels)/np.sum(unique_pixels))*100,".2f")
    
    # converting the image to bytes
    pil_im = Image.fromarray(new_img)
    buff = io.BytesIO()
    pil_im.save(buff, format="PNG")
    img_str = base64.b64encode(buff.getvalue())


    #return str(img_str, 'utf-8')
    return (str(img_str, 'utf-8'), hotspotPercentage)
    