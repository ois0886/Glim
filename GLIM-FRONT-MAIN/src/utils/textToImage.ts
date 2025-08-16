
export function textToImage(
  text: string,
  width: number,
  height: number,
  font: string = '48px Arial',
  textColor: string = '#000000',
  backgroundColor: string = '#ffffff',
  padding: number = 20
): string {
  const canvas = document.createElement('canvas');
  canvas.width = width;
  canvas.height = height;
  const ctx = canvas.getContext('2d');

  if (!ctx) {
    throw new Error('Failed to get 2D context');
  }

  // Fill background
  ctx.fillStyle = backgroundColor;
  ctx.fillRect(0, 0, width, height);

  // Set text properties
  ctx.font = font;
  ctx.fillStyle = textColor;
  ctx.textAlign = 'left'; // Align left for wrapping
  ctx.textBaseline = 'top'; // Align top for wrapping

  const words = text.split(' ');
  let line = '';
  const lines: string[] = [];
  const maxWidth = width - 2 * padding;
  const lineHeight = parseInt(font) * 1.2; // Estimate line height

  for (let n = 0; n < words.length; n++) {
    const testLine = line + words[n] + ' ';
    const metrics = ctx.measureText(testLine);
    const testWidth = metrics.width;
    if (testWidth > maxWidth && n > 0) {
      lines.push(line);
      line = words[n] + ' ';
    } else {
      line = testLine;
    }
  }
  lines.push(line); // Add the last line

  // Calculate total text height to center vertically
  const totalTextHeight = lines.length * lineHeight;
  let y = (height - totalTextHeight) / 2 + padding;

  for (let i = 0; i < lines.length; i++) {
    ctx.fillText(lines[i], padding, y);
    y += lineHeight;
  }

  return canvas.toDataURL();
}
