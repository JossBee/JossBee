nohup kubectl port-forward service/auth-server-nodeport-service 9191:9191 &
nohup kubectl port-forward service/authdb-nodeport-service 3306:3306 &
