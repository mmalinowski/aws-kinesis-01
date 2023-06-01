terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.0"
    }
  }

  backend "s3" {
    bucket  = "<<your-bucket-name>>"
    key     = "kinesis-data-analytics/terraform.tfstate"
    region  = "eu-west-1"
    encrypt = "true"
  }
}

provider "aws" {
  region = "eu-west-1"

  default_tags {
    tags = {
      project    = "kinesis-data-analytics"
      managed-by = "Terraform"
    }
  }
}
