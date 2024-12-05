FROM python:3.10-slim

WORKDIR /deepeval

COPY . /deepeval

RUN pip install fastapi uvicorn deepeval

WORKDIR /deepeval

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000", "--reload"]
