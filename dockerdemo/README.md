---
```
[Spring Boot App Code]
        |
        v
[Maven Build: Creates JAR]
        |
        v
[Docker Build]
(Dockerfile → Container Image)
        |
        v
[Push Image to DockerHub]
        |
        v
[Kubernetes Cluster]
        |
        ├── [Deployment.yaml]
        |      (Creates Pods using Docker Image)
        |
        └── [Service.yaml]
               (Exposes Pods via NodePort)
        |
        v
[Pods Running Spring Boot App]
        |
        v
[NodePort Service]
(Accessible via Minikube/Cluster IP + Port)

=============================================

[ArgoCD]
(Installed inside Kubernetes)
        |
        ├── Monitors GitHub Repo
        |   (Repo: Believekhandal/docker-k8s)
        |
        ├── Watches Path:
        |   /dockerdemo/kubernetes-manifests
        |
        └── Auto-Syncs YAML Changes to Kubernetes
            (Deployment/Service updated automatically)

=============================================

[Kubernetes Dashboard (UI)]
    |
    ├── Access using token
    ├── View Deployments, Pods, Services
    └── Monitor entire cluster visually

    
    
**Notes: Full End-to-End Setup (Spring Boot + Docker + Kubernetes + ArgoCD + Dashboard)**
```			   
---
---

# 1. **Docker Commands**

### Common Commands:

```bash
# Build Docker Image
docker build -t <image-name> .

# List Docker Images
docker images

# Run Docker Container
docker run -p 8080:8080 <image-name>

# Tag Docker Image
docker tag <image-id> <dockerhub-username>/<repo-name>:<tag>

# Push Docker Image to DockerHub
docker push <dockerhub-username>/<repo-name>:<tag>

# Remove Docker Image
docker rmi <image-id>

# List Docker Containers
docker ps -a

# Remove Docker Container
docker rm <container-id>

# Force Remove Docker Image
docker rmi -f <image-id>
```

---

# 2. **Kubernetes Commands**

```bash
# Start Minikube (if used)
minikube start

# Check Nodes
kubectl get nodes

# Create Deployment
kubectl apply -f deployment.yaml

# Create Service
kubectl apply -f service.yaml

# Check Pods
kubectl get pods

# Check Services
kubectl get services

# Check Deployments
kubectl get deployments

# Delete Deployment
kubectl delete deployment <deployment-name>

# Delete Service
kubectl delete service <service-name>

# Describe Pod/Service/Deployment
kubectl describe pod <pod-name>
kubectl describe service <service-name>
kubectl describe deployment <deployment-name>

# View Logs
kubectl logs <pod-name>
```

---

# 3. **Kubernetes YAML Files Explained**

## deployment.yaml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: k8s-docker-java
spec:
  replicas: 2  # Number of pod copies
  selector:
    matchLabels:
      app: k8s-docker-java
  template:
    metadata:
      labels:
        app: k8s-docker-java
    spec:
      containers:
      - name: springboot-container
        image: vk0docker/k8s-docker-java:latest
        ports:
        - containerPort: 8080
```

**Parameters:**

- `replicas`: How many pods to maintain.
- `selector`: Labels used to find matching pods.
- `template`: Pod template.
- `containers`: Docker image and container settings.

## service.yaml

```yaml
apiVersion: v1
kind: Service
metadata:
  name: k8s-docker-java
spec:
  type: NodePort
  selector:
    app: k8s-docker-java
  ports:
  - port: 8080
    targetPort: 8080
    nodePort: 31136
```

**Parameters:**

- `type: NodePort`: Exposes service on each Node's IP at a static port.
- `selector`: Connects service to matching pods.
- `port`: Service port.
- `targetPort`: Pod port.
- `nodePort`: External port.

## app.yaml (ArgoCD)

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: k8s-docker-java-app
  namespace: argocd
spec:
  project: default
  source:
    repoURL: 'https://github.com/Believekhandal/docker-k8s.git'
    targetRevision: HEAD
    path: dockerdemo/kubernetes-manifests
  destination:
    server: 'https://kubernetes.default.svc'
    namespace: default
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
```

**Parameters:**

- `repoURL`: GitHub repository URL.
- `path`: Folder where YAML files exist.
- `destination.server`: Kubernetes API server.
- `syncPolicy.automated`: Auto-sync and auto-prune old versions.

---

# 4. **Setup Kubernetes on Docker Desktop**

- Enable "Kubernetes" from Docker Desktop Settings.
- It installs:
  - Kubernetes Server & CLI (kubectl)
  - Default single-node cluster (local development).

Commands:

```bash
kubectl version
kubectl get nodes
```

---

# 5. **ArgoCD Setup Steps**

```bash
# Create namespace
kubectl create namespace argocd

# Install ArgoCD
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

# Port forward to access UI
kubectl port-forward svc/argocd-server -n argocd 8080:443

# Login credentials:
# Username: admin
# Password: (initial password is the pod name)
kubectl get pods -n argocd

# Change password after first login.
```

---

# 6. **Access Kubernetes Dashboard**

```bash
# Install Metrics Server (optional for full features)
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml

# Start Dashboard
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml

# Access Dashboard
kubectl proxy

# Open URL
http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/
```

Token Authentication:

```bash
# Create Service Account and ClusterRoleBinding manually.
kubectl create serviceaccount dashboard-admin-sa
kubectl create clusterrolebinding dashboard-admin-sa --clusterrole=cluster-admin --serviceaccount=default:dashboard-admin-sa

# Get Token
kubectl get secret $(kubectl get serviceaccount dashboard-admin-sa -o jsonpath="{.secrets[0].name}") -o go-template="{{.data.token | base64decode}}"
```

---

# 7. **Interview Questions (Commonly Asked)**

- Difference between Docker Image and Container?
- What is Kubernetes Deployment vs Service?
- What is NodePort vs ClusterIP vs LoadBalancer?
- What is ArgoCD and how it works?
- How is Auto-sync handled in ArgoCD?
- How to roll back a deployment in Kubernetes?
- How is token-based login handled in Kubernetes Dashboard?
- Explain how GitOps works with Kubernetes.
- What is replicaSet?
- What happens if a pod crashes?
- How will you monitor Kubernetes pods and services?

---

# 8. **Final Summary**

You have created:

- Dockerized Spring Boot application.
- Deployed it to Kubernetes.
- Automated deployment using ArgoCD.
- Monitored Kubernetes using Dashboard UI.
- Followed complete DevOps Cycle: Build → Test → Deploy → Monitor.

---

# 9. **Important kubectl Commands (Complete List)**

### Cluster & Node Info

```bash
kubectl cluster-info         # Show cluster info
kubectl get nodes             # List cluster nodes
kubectl describe node <name>  # Detailed info about a node
```

### Pod Management

```bash
kubectl get pods                      # List all pods
kubectl get pods -n <namespace>       # List pods in a namespace
kubectl describe pod <pod-name>       # Pod details
kubectl logs <pod-name>               # View pod logs
kubectl exec -it <pod-name> -- /bin/bash   # SSH into running pod
```

### Deployment Management

```bash
kubectl get deployments               # List deployments
kubectl describe deployment <name>    # Deployment details
kubectl rollout status deployment/<name>  # Check rollout status
kubectl rollout undo deployment/<name>    # Rollback to previous version
kubectl delete deployment <name>      # Delete a deployment
```

### Service Management

```bash
kubectl get services                  # List services
kubectl describe service <name>       # Service details
kubectl delete service <name>         # Delete service
```

### Apply / Delete YAMLs

```bash
kubectl apply -f <file.yaml>           # Apply any YAML config
kubectl delete -f <file.yaml>          # Delete resources in YAML
```

### Namespaces

```bash
kubectl get namespaces                # List all namespaces
kubectl create namespace <name>       # Create a new namespace
kubectl delete namespace <name>       # Delete a namespace
```

### Config and Context

```bash
kubectl config view                   # View kubeconfig settings
kubectl config use-context <context>  # Switch context
```

### Monitoring

```bash
kubectl top pods                      # Show pod resource usage (needs metrics-server)
kubectl top nodes                     # Show node resource usage
```

### Port Forwarding

```bash
kubectl port-forward svc/<service-name> <local-port>:<service-port>  # Expose service locally
```

---

# 📊 End of Notes

---

**(Now generating PDF version with all updated sections! 📄)**


    