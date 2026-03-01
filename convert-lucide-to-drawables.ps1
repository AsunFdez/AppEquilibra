# convert-lucide-to-drawables.ps1
Set-StrictMode -Version Latest

$srcRoot = "Lucide-icons"
$dstDir  = "Equilibra\app\src\main\res\drawable"
New-Item -ItemType Directory -Force -Path $dstDir | Out-Null

$prefixMap = @{
  "navigation"  = "nav"
  "category"    = "cat"
  "achievement" = "ach"
  "analysis"    = "ana"
  "brand"       = "brd"
  "feature"     = "fea"
  "onboarding"  = "onb"
  "settings"    = "set"
  "utility"     = "uti"
}

function SanitizeName([string]$name) {
  $n = $name.ToLower() -replace '[^a-z0-9]', '_'
  return ($n -replace '_{2,}', '_').Trim('_')
}

function Get-Attr([string]$tag, [string]$attr) {
  $pattern = [regex]::Escape($attr) + '\s*=\s*"(.*?)"'
  $m = [regex]::Match($tag, $pattern)
  if ($m.Success) { return $m.Groups[1].Value } else { return $null }
}

function Circle-To-PathData([string]$cx, [string]$cy, [string]$r) {
  [double]$cx = [double]$cx; [double]$cy = [double]$cy; [double]$r = [double]$r
  return ("M {0} {1} a {2} {2} 0 1 0 {3} 0 a {2} {2} 0 1 0 -{3} 0" -f ($cx-$r), $cy, $r, (2*$r))
}

Get-ChildItem -Path $srcRoot -Recurse -Filter *.svg | ForEach-Object {
  $svgPath = $_.FullName
  $folderName = Split-Path -Path (Split-Path $svgPath -Parent) -Leaf
  $prefix = if ($prefixMap.ContainsKey($folderName)) { $prefixMap[$folderName] } else { "ico" }

  $base = $_.BaseName
  $san = SanitizeName $base
  $outName = "ic_{0}_{1}.xml" -f $prefix, $san
  $outPath = Join-Path $dstDir $outName

  $svgText = Get-Content -Raw -Path $svgPath

  # viewBox
  $vbM = [regex]::Match($svgText, 'viewBox\s*=\s*"(.*?)"')
  if ($vbM.Success) {
    $parts = $vbM.Groups[1].Value -split '\s+'
    if ($parts.Count -ge 4) { $vx = $parts[2]; $vy = $parts[3] } else { $vx = 24; $vy = 24 }
  } else {
    $vx = 24; $vy = 24
  }

  $vecHeader = @"
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="$vx" android:viewportHeight="$vy">
"@

  $paths = ""

  # path elements
  $pathMatches = [regex]::Matches($svgText, '<path\b[^>]*\/?>', 'IgnoreCase')
  foreach ($m in $pathMatches) {
    $tag = $m.Value
    $d = Get-Attr $tag 'd'
    if (-not $d) { continue }
    $fill = Get-Attr $tag 'fill'
    $stroke = Get-Attr $tag 'stroke'
    $sw = Get-Attr $tag 'stroke-width'
    $lc = Get-Attr $tag 'stroke-linecap'
    $lj = Get-Attr $tag 'stroke-linejoin'

    $fillAttr = if ($fill -and $fill -ne 'none') { "android:fillColor=`"$fill`"" } else { 'android:fillColor="#00000000"' }
    $strokeAttr = if ($stroke -and $stroke -ne 'currentColor') { "android:strokeColor=`"$stroke`"" } else { 'android:strokeColor="#FF000000"' }
    $swAttr = if ($sw) { " android:strokeWidth=`"$sw`"" } else { ' android:strokeWidth="2"' }
    $lcAttr = if ($lc) { " android:strokeLineCap=`"$lc`"" } else { ' android:strokeLineCap="round"' }
    $ljAttr = if ($lj) { " android:strokeLineJoin=`"$lj`"" } else { ' android:strokeLineJoin="round"' }

    $paths += "  <path $fillAttr $strokeAttr$swAttr$lcAttr$ljAttr android:pathData=`"$d`" />`n"
  }

  # circle elements
  $circleMatches = [regex]::Matches($svgText, '<circle\b[^>]*\/?>', 'IgnoreCase')
  foreach ($m in $circleMatches) {
    $tag = $m.Value
    $cx = Get-Attr $tag 'cx'; $cy = Get-Attr $tag 'cy'; $r = Get-Attr $tag 'r'
    if (-not ($cx -and $cy -and $r)) { continue }
    $d = Circle-To-PathData $cx $cy $r
    $fill = Get-Attr $tag 'fill'
    $stroke = Get-Attr $tag 'stroke'
    $sw = Get-Attr $tag 'stroke-width'
    $lc = Get-Attr $tag 'stroke-linecap'
    $lj = Get-Attr $tag 'stroke-linejoin'

    $fillAttr = if ($fill -and $fill -ne 'none') { "android:fillColor=`"$fill`"" } else { 'android:fillColor="#00000000"' }
    $strokeAttr = if ($stroke -and $stroke -ne 'currentColor') { "android:strokeColor=`"$stroke`"" } else { 'android:strokeColor="#FF000000"' }
    $swAttr = if ($sw) { " android:strokeWidth=`"$sw`"" } else { ' android:strokeWidth="2"' }
    $lcAttr = if ($lc) { " android:strokeLineCap=`"$lc`"" } else { ' android:strokeLineCap="round"' }
    $ljAttr = if ($lj) { " android:strokeLineJoin=`"$lj`"" } else { ' android:strokeLineJoin="round"' }

    $paths += "  <path $fillAttr $strokeAttr$swAttr$lcAttr$ljAttr android:pathData=`"$d`" />`n"
  }

  if (-not $paths) {
    $inner = ($svgText -replace '<\/?svg[^>]*>','').Trim()
    if ($inner) { $paths = "  <!-- Converted content; review if necessary -->`n  $inner`n" }
  }

  $vecFooter = "</vector>`n"
  $content = $vecHeader + $paths + $vecFooter

  $content | Out-File -FilePath $outPath -Encoding utf8 -Force
  Write-Host "Wrote: $outPath"
}

Write-Host "Conversion completa. Verifica los XML en $dstDir"