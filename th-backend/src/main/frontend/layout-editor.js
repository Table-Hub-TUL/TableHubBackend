import interact from 'interactjs';

(function () {

    /**
     * Initializes drag-and-drop functionality on elements with the 'draggable-item' class.
     * @param {HTMLElement} vaadinLayoutComponent - The Vaadin component for server callbacks.
     */
    window.initDraggables = (vaadinLayoutComponent) => {

        interact('.draggable-item')
            .draggable({
                inertia: false,
                autoScroll: true,
                ignoreFrom: '.ignore-drag',

                listeners: {
                    /**
                     * Called every time the mouse moves during a drag.
                     */
                    move(event) {
                        const target = event.target;

                        const x = (parseFloat(target.getAttribute('data-x')) || 0) + event.dx;
                        const y = (parseFloat(target.getAttribute('data-y')) || 0) + event.dy;

                        target.style.transform = `translate(${x}px, ${y}px)`;

                        target.setAttribute('data-x', x);
                        target.setAttribute('data-y', y);
                    },

                    /**
                     * Called when the user lets go of the mouse.
                     * This is where we save the position.
                     */
                    end(event) {
                        const target = event.target;

                        if (!event.dx && !event.dy) {
                            return;
                        }

                        const canvas = document.getElementById('canvas');

                        if (!canvas) {
                            console.error("Canvas element not found!");
                            return;
                        }

                        const canvasRect = canvas.getBoundingClientRect();
                        const itemRect = target.getBoundingClientRect();

                        let finalX = (parseFloat(target.style.left) || 0) + (parseFloat(target.getAttribute('data-x')) || 0);
                        let finalY = (parseFloat(target.style.top) || 0) + (parseFloat(target.getAttribute('data-y')) || 0);

                        const itemWidth = itemRect.width;
                        const itemHeight = itemRect.height;

                        if (finalX < 0) {
                            finalX = 0;
                        }
                        if (finalX + itemWidth > canvas.clientWidth) {
                            finalX = canvas.clientWidth - itemWidth;
                        }

                        if (finalY < 0) {
                            finalY = 0;
                        }
                        if (finalY + itemHeight > canvas.clientHeight) {
                            finalY = canvas.clientHeight - itemHeight;
                        }

                        const itemId = target.getAttribute('data-item-id');
                        const itemType = target.getAttribute('data-item-type');

                        target.style.transform = '';
                        target.removeAttribute('data-x');
                        target.removeAttribute('data-y');
                        target.style.left = finalX + 'px';
                        target.style.top = finalY + 'px';

                        if (itemType === 'table') {
                            const coordSpan = target.querySelector('span:last-child');
                            if(coordSpan) {
                                coordSpan.textContent = `X:${Math.round(finalX)}, Y:${Math.round(finalY)}`;
                            }
                        }

                        vaadinLayoutComponent.$server.updateItemPosition(itemType, itemId, finalX, finalY);
                    }
                }
            });
    };
})();