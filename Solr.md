# Solr

Using a local Solr instance is convenient for testing and development and using Docker makes running a Solr instance trivial

```bash
docker run -p 8983:8983 --name solr solr
```

To make the Solr indices persist between containers we need to have Solr save its configuration and data outside the container. We will do this by bind mounting `/var`and `/opt`inside the container to a directory outside the container.  To make this easier we first copy the contents of `/var` and `/opt`out of the container.

For example, if we want to store the Solr data in `/usr/index`:
```bash
docker run -t -v /usr/index:/host solr cp -r /opt /host
docker run -t -v /usr/index:/host solr cp -r /var/solr /host/var
```
We mount the `/usr/index` as `/host` inside the container so we can copy the directories. When we are done the `/usr/index/opt` and `/usr/index/var` directories will  contain the contents of the `/opt` and `/var/solr` diirectories from the container.  Now we bind mount these when starting the Solr container

```bash
docker run -d -v /usr/index/opt:/opt -v /usr/index/var:/var/solr ...
```

