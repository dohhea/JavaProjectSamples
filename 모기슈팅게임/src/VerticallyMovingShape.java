import java.net.URL;



public class VerticallyMovingShape extends Shape {
	public VerticallyMovingShape(URL imgURL, int x, int y, int margin, int steps, int xBoundary, int yBoundary) {
		// imgPath : 그림 파일의 경로명
		// x, y : 이미지의 위치 좌표
		// margin : 이 이미지의 영역을 나타내는 범위 (이 영역안에 있으면 충돌 한 것으로 판단 하기 위함)
		// steps : 이미지가 움직일때 이동하는 좌표 단위
		// xBoundary, yBoundary : 그림이 이동할 수 있는 좌표의 최대값
		super (imgURL, x, y, margin, steps, xBoundary, yBoundary);
	}
	
	public VerticallyMovingShape(URL imgURL, int margin, int steps, int xBoundary, int yBoundary) {
		super (imgURL, margin, steps, xBoundary, yBoundary);
	}

	public void move() {
		if (xDirection > 0 && x >= xBoundary) {
			xDirection = -1;
		}
		if (xDirection < 0 && x <= 0) {
			xDirection = 1;
		}

		if (yDirection > 0 && y >= yBoundary) {
			yDirection = -1;
			x += (xDirection * steps * 5);
		}
		if (yDirection < 0 && y <= 0) {
			yDirection = 1;
			x += (xDirection * steps * 5);
		}

		y += (yDirection * steps);
	}
}
