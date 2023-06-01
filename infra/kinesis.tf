resource "aws_s3_bucket" "kda_scripts_bucket" {
  bucket = "<<your-bucket-name>>"
}

resource "aws_s3_object" "kda_fraud_detector_app" {
  key         = "apps/flink/fraud-detector-0.1.jar"
  bucket      = aws_s3_bucket.kda_scripts_bucket.id
  source      = "../froud-detector/build/libs/froud-detector-0.1-all.jar"
  source_hash = filemd5("../froud-detector/build/libs/froud-detector-0.1-all.jar")
}

resource "aws_kinesis_stream" "transactions" {
  name             = "transactions"
  shard_count      = 1
  retention_period = 24

  stream_mode_details {
    stream_mode = "PROVISIONED"
  }
}

resource "aws_kinesis_stream" "locked_cards" {
  name             = "locked-cards"
  shard_count      = 1
  retention_period = 24

  stream_mode_details {
    stream_mode = "PROVISIONED"
  }
}

resource "aws_kinesis_stream" "alerts" {
  name             = "alerts"
  shard_count      = 1
  retention_period = 24

  stream_mode_details {
    stream_mode = "PROVISIONED"
  }
}

data "aws_iam_policy_document" "kda_scripts_bucket_policy" {
  statement {
    sid       = "AllowReadScript"
    actions   = ["s3:Get*", "s3:List*"]
    resources = ["${aws_s3_bucket.kda_scripts_bucket.arn}", "${aws_s3_bucket.kda_scripts_bucket.arn}/*"]
  }
  statement {
    sid       = "AllowListShards"
    actions   = ["kinesis:ListShards"]
    resources = ["*"]
  }
}

data "aws_iam_policy_document" "kinesis_assume_role_policy" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["kinesisanalytics.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "fraud_detector_job_role" {
  name               = "fraud-detector-job-role"
  assume_role_policy = data.aws_iam_policy_document.kinesis_assume_role_policy.json
}

resource "aws_iam_role_policy_attachment" "fraud_detector_kinesis_service_attachment" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonKinesisAnalyticsFullAccess"
  role       = aws_iam_role.fraud_detector_job_role.name
}

resource "aws_iam_role_policy_attachment" "fraud_detector_kinesis_full_access_attachment" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonKinesisFullAccess"
  role       = aws_iam_role.fraud_detector_job_role.name
}

resource "aws_iam_role_policy" "fraud_detector_job_role_allow_s3_read" {
  name   = "allow-s3-bucket-policy"
  role   = aws_iam_role.fraud_detector_job_role.name
  policy = data.aws_iam_policy_document.kda_scripts_bucket_policy.json
}

resource "aws_kinesisanalyticsv2_application" "fraud_detector" {
  name                   = "fraud-detector-application"
  runtime_environment    = "FLINK-1_15"
  service_execution_role = aws_iam_role.fraud_detector_job_role.arn

  application_configuration {
    application_code_configuration {
      code_content {
        s3_content_location {
          bucket_arn = aws_s3_bucket.kda_scripts_bucket.arn
          file_key   = aws_s3_object.kda_fraud_detector_app.key
        }
      }

      code_content_type = "ZIPFILE"
    }

    environment_properties {
      property_group {
        property_group_id = "FraudDetectorConfiguration"
        property_map = {
          transactionsStreamName     = aws_kinesis_stream.transactions.name
          lockedCardsStreamName      = aws_kinesis_stream.locked_cards.name
          alertsStreamName           = aws_kinesis_stream.alerts.name
          region                     = "eu-west-1"
          excessiveTransactionWindow = "60"
          excessiveTransactionCount  = "5"
          scamDetectorSmallAmount    = "1"
          scamDetectorLargeAmount    = "950"
          scamDetectorTime           = "30"
        }
      }
    }

    flink_application_configuration {
      checkpoint_configuration {
        configuration_type = "DEFAULT"
      }

      monitoring_configuration {
        configuration_type = "CUSTOM"
        log_level          = "INFO"
        metrics_level      = "TASK"
      }

      parallelism_configuration {
        auto_scaling_enabled = false
        configuration_type   = "CUSTOM"
        parallelism          = 1
        parallelism_per_kpu  = 1
      }
    }
  }
}
