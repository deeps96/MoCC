OS: MacOS 10.15.1

https://docs.aws.amazon.com/cli/latest/userguide/install-macos.html
http://www.studytrails.com/amazon-aws/create-aws-ec2-instance-using-cli/

- `curl "https://s3.amazonaws.com/aws-cli/awscli-bundle.zip" -o "awscli-bundle.zip"`
- `unzip awscli-bundle.zip`
- `sudo ./awscli-bundle/install -i /usr/local/aws -b /usr/local/bin/aws`

- `aws configure`
  - AWS Access Key
  - AWS Secret Access Key
  - Default region name: region=us-east-1
  - Default output format: json
- `export AWS_SESSION_TOKEN=<>`
- `export AWS_ACCESS_KEY_ID=<>`
- `export AWS_SECRET_ACCESS_KEY=<>`
taken from https://labs.vocareum.com/main/main.php?m=editor&nav=1&asnid=14334&stepid=14335 -> Account Details

- `aws ec2 create-security-group --group-name mocc --description "MoCC security group"`
-> returns GroupId
- `aws ec2 authorize-security-group-ingress --group-name mocc --protocol tcp --port 22 --cidr 0.0.0.0/0`

https://aws.amazon.com/de/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc
-> free instance type: t2.micro

- `aws ec2 describe-images --filters 'Name=architecture, Values=x86_64' 'Name=state, Values=available' 'Name=owner-alias, Values=amazon' 'Name=is-public, Values=true' 'Name=image-type, Values=machine' 'Name=name, Values=*ami*' --query 'sort_by(Images, &CreationDate)'`

take second from bottom (bottom has most recent, but is minified version)
ami-00dc79254d0461090

https://docs.aws.amazon.com/cli/latest/reference/ec2/import-key-pair.html

- `aws ec2 import-key-pair --key-name MacBookPro --public-key-material file://~/.ssh/id_rsa.pub`

`aws ec2 run-instances --image-id ami-00dc79254d0461090 --key-name MacBookPro --instance-type t2.micro --count 1 --security-groups mocc`


Status can be fetched using
`aws ec2 describe-instances`

SSH
`ssh ec2-user@<public dns from above command>`

Hardware:
1 CPU
1GB RAM
