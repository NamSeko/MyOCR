import uvicorn
from fastapi import FastAPI, UploadFile, File
import cv2
import numpy as np

app = FastAPI()

@app.post("/ocr")
async def ocr_process(file: UploadFile = File(...)):
    # 1. Đọc ảnh từ Java gửi sang
    contents = await file.read()
    nparr = np.frombuffer(contents, np.uint8)
    img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

    # 2. Chạy Model PaddleOCR (Giả lập kết quả tại đây)
    # result = ocr_model.ocr(img)
    
    print(f"Đã nhận ảnh: {file.filename}, kích thước: {img.shape}")
    
    # 3. Trả kết quả JSON về cho Java
    return {
        "status": "success",
        "id_card": "038099001234",
        "name": "NGUYEN VAN A",
        "dob": "01/01/2000"
    }

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)