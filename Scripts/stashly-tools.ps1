function Send-Resp($text) {
  $c = New-Object System.Net.Sockets.TcpClient("localhost",6380)
  $s = $c.GetStream()
  $b = [System.Text.Encoding]::UTF8.GetBytes($text)
  $s.Write($b,0,$b.Length)
  $buf = New-Object byte[] 4096
  $n = $s.Read($buf,0,4096)
  $reply = [System.Text.Encoding]::UTF8.GetString($buf,0,$n)
  $c.Close()
  $reply.Replace("`r`n","\r\n")
}

function Stash {
  $text = "*$($args.Count)`r`n"
  foreach ($p in $args) {
    $len = [System.Text.Encoding]::UTF8.GetByteCount($p)
    $text += "`$$len`r`n$p`r`n"
  }
  Send-Resp $text
}