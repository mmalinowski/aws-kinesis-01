FROM amazonlinux:2023

RUN dnf install -y aws-kinesis-agent which findutils initscripts nodejs

COPY ./kinesis/agent.json /etc/aws-kinesis/agent.json

WORKDIR /usr/src/app

COPY ["package.json", "package-lock.json", "tsconfig.json", "./"]

COPY ./src ./src

COPY ./start.sh start.sh 
RUN chmod +x start.sh

# Installs all packages
RUN npm install

# Runs the dev npm script to build & start the server
ENTRYPOINT [ "./start.sh" ]