# Kalky Backend — Hetzner VPS Deployment

Deploy the Kalky backend on a Hetzner Cloud CAX11 (2 vCPU ARM64, 4 GB RAM, 40 GB disk, ~€3.85/mo).

---

## 1. Create the Server

1. Go to [Hetzner Cloud Console](https://console.hetzner.cloud/)
2. **New Server** → Location: your choice → Image: **Ubuntu 24.04** → Type: **CAX11 (ARM64)**
3. Add your SSH key
4. Create & note the IP address

## 2. Basic Server Hardening

```bash
ssh root@YOUR_IP

# Update system
apt update && apt upgrade -y

# Firewall
ufw allow OpenSSH
ufw allow 80/tcp
ufw allow 443/tcp
ufw enable

# Disable root password login
sed -i 's/^#\?PermitRootLogin.*/PermitRootLogin prohibit-password/' /etc/ssh/sshd_config
sed -i 's/^#\?PasswordAuthentication.*/PasswordAuthentication no/' /etc/ssh/sshd_config
systemctl restart sshd
```

## 3. Create Deploy User

```bash
adduser --disabled-password --gecos "" deploy
usermod -aG sudo deploy
usermod -aG docker deploy

# Copy SSH keys from root
mkdir -p /home/deploy/.ssh
cp /root/.ssh/authorized_keys /home/deploy/.ssh/
chown -R deploy:deploy /home/deploy/.ssh

# Allow passwordless sudo for deploy (optional, for convenience)
echo "deploy ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/deploy
```

## 4. Install Docker

```bash
apt install -y docker.io docker-compose-plugin

# Verify
docker --version
docker compose version
```

## 5. Clone & Configure

```bash
# As deploy user
su - deploy

git clone https://github.com/YOUR_USER/kalky.git /opt/kalky
cd /opt/kalky/backend

# Create .env from example
cp .env.example .env
nano .env
```

Set your values in `.env`:

```
OPENAI_API_KEY=sk-your-real-key
PORT=3000
ADMIN_KEY=generate-a-random-string-here
```

## 6. Configure Domain

Edit `backend/Caddyfile` — replace `your-domain.com` with your actual domain:

```
api.kalky.cz {
    reverse_proxy backend:3000
}
```

Point your domain's DNS A record to the server IP.

## 7. Start Services

```bash
cd /opt/kalky/backend
docker compose up -d --build
```

Caddy will automatically obtain a Let's Encrypt TLS certificate for your domain.

Verify:

```bash
# Check containers are running
docker compose ps

# Check logs
docker compose logs -f

# Test health endpoint
curl https://your-domain.com/health
# → {"status":"ok"}
```

## 8. Import Product Data

If you have a product JSON file to import:

```bash
# Copy the JSON file to the server, then:
docker compose exec backend bun run scripts/import.ts /app/data/products.json
```

Or use the admin API:

```bash
curl -X POST https://your-domain.com/api/admin/import \
  -H "Authorization: Bearer YOUR_ADMIN_KEY" \
  -H "Content-Type: application/json" \
  -d @products.json
```

## 9. Updates

Pull latest code and rebuild:

```bash
cd /opt/kalky/backend
git pull
docker compose up -d --build
```

## 10. Monitoring

```bash
# View logs (follow)
docker compose logs -f backend

# Check container status
docker compose ps

# Health check
curl https://your-domain.com/health

# Restart
docker compose restart
```

---

## CI/CD — Auto-deploy on Push

The GitHub Actions workflow (`.github/workflows/backend.yml`) includes a deploy job that automatically deploys to the VPS when CI passes on `main`/`master`.

### Setup

1. **Generate an SSH keypair** for the deploy user:

   ```bash
   ssh-keygen -t ed25519 -f deploy_key -N ""
   ```

2. **Add the public key** to the VPS:

   ```bash
   cat deploy_key.pub >> /home/deploy/.ssh/authorized_keys
   ```

3. **Add GitHub repo secrets** (Settings → Secrets and variables → Actions):

   | Secret           | Value                                |
   |------------------|--------------------------------------|
   | `DEPLOY_HOST`    | VPS IP or domain (e.g. `65.21.x.x`) |
   | `DEPLOY_USER`    | `deploy`                             |
   | `DEPLOY_SSH_KEY` | Contents of `deploy_key` (private)   |

4. Push to `main`/`master` — the deploy job will SSH in and run:

   ```bash
   cd /opt/kalky/backend && git pull && docker compose up -d --build
   ```

### Troubleshooting

- **SSH connection refused**: Check `ufw` allows SSH, verify the key is in `authorized_keys`
- **Docker permission denied**: Ensure `deploy` user is in the `docker` group (`usermod -aG docker deploy`), then re-login
- **Caddy cert errors**: Ensure DNS points to the server and ports 80/443 are open
- **Container keeps restarting**: Check `docker compose logs backend` for errors
