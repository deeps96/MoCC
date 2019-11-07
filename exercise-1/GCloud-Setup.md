OS: MacOS 10.15.1

https://cloud.google.com/compute/docs/gcloud-compute/?hl=de

-> Installation via [Google Cloud SDK](https://cloud.google.com/sdk/docs/?hl=de)

Choose a working directory (we are using a GitHub Repository for this)

- `curl -O https://dl.google.com/dl/cloudsdk/channels/rapid/downloads/google-cloud-sdk-245.0.0-darwin-x86_64.tar.gz`
- `gunzip -c google-cloud-sdk-245.0.0-darwin-x86_64.tar.gz | tar xopf -`
- `rm google-cloud-sdk-245.0.0-darwin-x86_64.tar.gz`
- `./google-cloud-sdk/install.sh`
- Restart terminal
- `gcloud components update`
- `gcloud init`
  - Y - Login using Browser
  - [2] Create a new project
    - mocc19 as project id
- `gcloud projects list`
- `gcloud config set project <project-id>`
- `gcloud compute instances create mocc-1 --image-project debian-cloud --image-family debian-10`
  - 25 - choose region europe-west1-b
- `gcloud beta compute --project "mocc19-258314" ssh --zone "europe-west1-b" "mocc-1"`
  - [Y] install beta
  - enter passphrase
  - [Y] enable API
- `gcloud compute instances stop mocc-1`
- ``
