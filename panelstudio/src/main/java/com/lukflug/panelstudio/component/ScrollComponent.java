package com.lukflug.panelstudio.component;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import com.lukflug.panelstudio.base.Context;

/**
 * A component that can scroll another component.
 * @author lukflug
 */
public abstract class ScrollComponent extends ComponentProxy {
	/**
	 * Current position scrolling position.
	 */
	protected Point scrollPos=new Point(0,0);
	
	/**
	 * Constructor.
	 * @param component the component to scroll
	 */
	public ScrollComponent(IComponent component) {
		super(component);
	}
	
	@Override
	public void render(Context context) {
		getHeight(context);
		context.getInterface().window(context.getRect());
		doOperation(context,component::render);
		context.getInterface().restore();
	}

	@Override
	public void handleButton(Context context, int button) {
		doOperation(context,subContext->component.handleButton(subContext,button));
	}

	@Override
	public void handleKey(Context context, int scancode) {
		doOperation(context,subContext->component.handleKey(subContext,scancode));
	}

	@Override
	public void handleScroll(Context context, int diff) {
		AtomicReference<Context> sContext=new AtomicReference<Context>();
		doOperation(context,subContext->{
			component.handleScroll(subContext,diff);
			sContext.set(subContext);
		});
		if (context.isHovered()) {
			if (sContext.get().getSize().height>context.getSize().height) scrollPos.translate(0,diff);
			else if (sContext.get().getSize().width>context.getSize().width) scrollPos.translate(diff,0);
			clampScrollPos(context,sContext.get());
		}
	}

	@Override
	public void getHeight(Context context) {
		doOperation(context,component::getHeight);
	}
	
	/**
	 * Perform a context-sensitive operation.
	 * @param context the context to use
	 * @param operation the operation to perform
	 */
	protected void doOperation (Context context, Consumer<Context> operation) {
		Context subContext=getContext(context);
		operation.accept(subContext);
		if (subContext.focusReleased()) context.releaseFocus();
		else if (subContext.foucsRequested()) context.requestFocus();
		context.setHeight(getScrollHeight(subContext.getSize().height));
	}
	
	/**
	 * Get the context for the content component.
	 * @param context the parent context
	 * @return the child context
	 */
	protected Context getContext (Context context) {
		return new Context(context,getComponentWidth(context.getSize().width),new Point(-scrollPos.x,-scrollPos.y),context.hasFocus(),context.onTop());
	}
	
	/**
	 * Clamp scroll position.
	 * @param context the parent context
	 * @param subContext the child context
	 */
	protected void clampScrollPos (Context context, Context subContext) {
		if (scrollPos.x>subContext.getSize().width-context.getSize().width) scrollPos.x=subContext.getSize().width-context.getSize().width;
		if (scrollPos.x<0) scrollPos.x=0;
		if (scrollPos.y>subContext.getSize().height-context.getSize().height) scrollPos.y=subContext.getSize().height-context.getSize().height;
		if (scrollPos.y<0) scrollPos.y=0;
	}
	
	/**
	 * Function to determine the width allocated to the child component.
	 * @param scrollWidth the visible width
	 * @return the component width
	 */
	protected abstract int getComponentWidth (int scrollWidth);
	
	/**
	 * Function to determine visible height.
	 * @param componentHeight the component height
	 * @return the visible height
	 */
	protected abstract int getScrollHeight (int componentHeight);
}