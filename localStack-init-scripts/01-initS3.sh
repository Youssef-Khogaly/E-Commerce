#!/bin/bash


# Check if the bucket already exists
if awslocal s3api head-bucket --bucket "$BUCKET_NAME" 2>/dev/null; then
  echo "Bucket '$BUCKET_NAME' already exists. Skipping creation."
else
  echo "Bucket '$BUCKET_NAME' does not exist. Creating..."
  awslocal s3 mb s3://"$BUCKET_NAME"
fi