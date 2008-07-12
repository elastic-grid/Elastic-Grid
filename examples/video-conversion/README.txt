This example is based on the great tutorial available here:
http://developer.amazonwebservices.com/connect/entry.jspa?externalID=691&categoryID=100

Steps performed on the client side:
1/ upload local videos to Amazon S3
2/ send a message to Amazon SQS queue for notification of video to be converted

Steps performed on the EC2 side:
3/ fetch one Amazon SQS notification of videos to be converted from the queue
4/ download the video locally from Amazon S3
5/ generate 5 conversions from this video:
    5.1/ convert the video
    5.2/ upload the converted video
6/ send a message to Amazon SQS queue for notification of video converted

Steps performed on the client side:
