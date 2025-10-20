(function () {
    let points = [];
    let isDrawing = false;
    let vaadinComponent = null;

    let previewLine = null;
    let wallPath = null;

    /**
     * Toggles the drawing mode on or off.
     * Called from LayoutEditorView.java.
     */
    window.toggleWallDrawing = (component, startDrawing) => {
        vaadinComponent = component;
        const canvas = vaadinComponent.querySelector("#canvas");

        isDrawing = startDrawing;
        points = [];
        let svg = getOrCreateDrawingSvg(canvas);

        if (isDrawing) {
            canvas.style.cursor = 'crosshair';
            canvas.addEventListener('click', handleCanvasClick);
            canvas.addEventListener('mousemove', handleMouseMove);

            wallPath = document.createElementNS('http://www.w3.org/2000/svg', 'path');
            wallPath.setAttribute('fill', 'none');
            wallPath.setAttribute('stroke', '#888'); // Style to match the saved wall
            wallPath.setAttribute('stroke-width', '3');
            svg.appendChild(wallPath);

            previewLine = document.createElementNS('http://www.w3.org/2000/svg', 'line');
            previewLine.setAttribute('stroke', '#007bff');
            previewLine.setAttribute('stroke-dasharray', '5, 5');
            previewLine.setAttribute('stroke-width', '2');
            svg.appendChild(previewLine);

        } else {
            canvas.style.cursor = 'default';
            canvas.removeEventListener('click', handleCanvasClick);
            canvas.removeEventListener('mousemove', handleMouseMove);

            if (previewLine) {
                previewLine.remove();
                previewLine = null;
            }
            if (wallPath) {
                wallPath.remove();
                wallPath = null;
            }
        }
    };

    function handleCanvasClick(event) {
        if (!isDrawing || !vaadinComponent) return;

        if (event.target.classList.contains('draggable-item')) {
            return;
        }

        const canvas = event.currentTarget;
        const rect = canvas.getBoundingClientRect();
        const x = event.clientX - rect.left;
        const y = event.clientY - rect.top;

        points.push({ x, y });

        const pathString = generatePathString();

        if (wallPath) {
            wallPath.setAttribute('d', pathString);
        }

        vaadinComponent.$server.updateShapePath(pathString);
    }

    function handleMouseMove(event) {
        if (!isDrawing || points.length === 0 || !previewLine) return;
        const canvas = event.currentTarget;
        const rect = canvas.getBoundingClientRect();
        const lastPoint = points[points.length - 1];

        previewLine.setAttribute('x1', lastPoint.x);
        previewLine.setAttribute('y1', lastPoint.y);
        previewLine.setAttribute('x2', event.clientX - rect.left);
        previewLine.setAttribute('y2', event.clientY - rect.top);
    }

    function generatePathString() {
        if (points.length === 0) return "";

        let path = `M ${points[0].x} ${points[0].y}`;
        for (let i = 1; i < points.length; i++) {
            path += ` L ${points[i].x} ${points[i].y}`;
        }
        return path;
    }

    function getOrCreateDrawingSvg(canvas) {
        let svg = canvas.querySelector("#drawing-svg");
        if (!svg) {
            let svgHtml = `<svg id='drawing-svg' width='100%' height='100%' style='position:absolute; top:0; left:0; z-index: 5;'></svg>`;
            canvas.insertAdjacentHTML('beforeend', svgHtml);
            svg = canvas.querySelector("#drawing-svg");
        }
        return svg;
    }

})();