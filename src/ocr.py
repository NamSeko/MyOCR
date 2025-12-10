import uvicorn
import numpy as np
import cv2
from fastapi import FastAPI, File, UploadFile, HTTPException
from paddleocr import PaddleOCR

app = FastAPI(title="AI Inference API")

ocr = PaddleOCR(
    text_recognition_model_name="PP-OCRv5_mobile_rec",
    text_recognition_model_dir="../inference_76",
    use_textline_orientation=False,
    use_doc_orientation_classify=False,
    use_doc_unwarping=False,
)

def run_ai_model(image_np):
    height, width, _ = image_np.shape
    result = ocr.predict(image_np)
    return {
        "width": width,
        "height": height,
        "texts": result[0]['rec_texts'], 
    }

@app.post("/predict")
async def predict_image(file: UploadFile = File(...)):
    if not file.content_type.startswith("image/"):
        raise HTTPException(status_code=400, detail="File gửi lên không phải là ảnh.")

    try:
        contents = await file.read()
        
        nparr = np.frombuffer(contents, np.uint8)
        
        img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
        
        if img is None:
            raise HTTPException(status_code=400, detail="Ảnh bị lỗi, không thể đọc được.")

        result = run_ai_model(img)
        
        return {
            "status": "success",
            "filename": file.filename,
            "data": result
        }

    except Exception as e:
        return {"status": "error", "message": str(e)}

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)