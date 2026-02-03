import cv2
import numpy as np
import time
from ultralytics import YOLO
from sort import Sort
model = YOLO("best.pt")

cap = cv2.VideoCapture(8)
if not cap.isOpened():
    print("Error al abrir la cámara.")
    exit()

tracker = Sort(max_age=5, min_hits=1, iou_threshold=0.3)

while True:
    ret, frame = cap.read()
    if not ret:
        print("No se pudo obtener el frame.")
        break

    results = model(frame)

    detections = []
    item = 1
    for box in results[0].boxes:
        x1, y1, x2, y2 = map(int, box.xyxy[0])
        conf = float(box.conf[0])
        detections.append([x1, y1, x2, y2, conf, item])
        item += 1
    detections_np = np.array(detections) if len(detections) > 0 else np.empty((0, 5))
    
    tracks = tracker.update(detections_np)

    annotated_frame = frame.copy()
    
    for track in tracks:
        x1, y1, x2, y2, track_id, item2 = track
        x1, y1, x2, y2, track_id = int(x1), int(y1), int(x2), int(y2), int(track_id)
        
        cv2.rectangle(annotated_frame, (x1, y1), (x2, y2), (0, 255, 0), 2)
        
        text = f"ID: {item2}"
        text_size, _ = cv2.getTextSize(text, cv2.FONT_HERSHEY_SIMPLEX, 1.5, 3)
        text_x = x2 + 10
        text_y = y1 + text_size[1]
        cv2.putText(annotated_frame, text, (text_x, text_y), cv2.FONT_HERSHEY_SIMPLEX, 1.5, (0, 255, 0), 3, cv2.LINE_AA)
    
    cv2.imshow("Detecciones y seguimiento SORT", annotated_frame)
    
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()