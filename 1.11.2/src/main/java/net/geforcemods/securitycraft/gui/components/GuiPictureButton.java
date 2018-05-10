package net.geforcemods.securitycraft.gui.components;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiPictureButton extends GuiButton{

	private final RenderItem itemRenderer;
	private Block blockToRender;
	private Item itemToRender;
	private ResourceLocation textureLocation;
	private int u;
	private int v;
	private int texWidth;
	private int texHeight;

	public GuiPictureButton(int id, int xPos, int yPos, int width, int height, RenderItem par7, ItemStack itemToRender) {
		super(id, xPos, yPos, width, height, "");
		itemRenderer = par7;

		if(!itemToRender.isEmpty() && itemToRender.getItem().getUnlocalizedName().startsWith("tile."))
			blockToRender = Block.getBlockFromItem(itemToRender.getItem());
		else
			this.itemToRender = itemToRender.getItem();
	}

	public GuiPictureButton(int id, int xPos, int yPos, int width, int height, ResourceLocation texture, int textureX, int textureY, int textureWidth, int textureHeight)
	{
		super(id, xPos, yPos, width, height, "");

		itemRenderer = null;
		textureLocation = texture;
		u = textureX;
		v = textureY;
		texWidth = textureWidth;
		texHeight = textureHeight;
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void drawButton(Minecraft par1, int par2, int par3)
	{
		if (visible)
		{
			FontRenderer var4 = par1.fontRenderer;
			par1.getTextureManager().bindTexture(BUTTON_TEXTURES);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			hovered = par2 >= x && par3 >= y && par2 < x + width && par3 < y + height;
			int var5 = getHoverState(hovered);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			this.drawTexturedModalRect(x, y, 0, 46 + var5 * 20, width / 2, height);
			this.drawTexturedModalRect(x + width / 2, y, 200 - width / 2, 46 + var5 * 20, width / 2, height);

			if(blockToRender != null){
				GL11.glEnable(GL12.GL_RESCALE_NORMAL); //(this.width / 2) - 8
				itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(blockToRender), x + 2, y + 3);
				itemRenderer.renderItemOverlayIntoGUI(par1.fontRenderer, new ItemStack(blockToRender), x + 2, y + 3, "");
			}else if(itemToRender != null){
				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
				itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(itemToRender), x + 2, y + 2);
				itemRenderer.renderItemOverlayIntoGUI(par1.fontRenderer, new ItemStack(itemToRender), x + 2, y + 2, "");
				GL11.glDisable(GL11.GL_LIGHTING);
			}
			else if(textureLocation != null)
			{
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				par1.getTextureManager().bindTexture(textureLocation);
				drawTexturedModalRect(x, y + 1, u, v, texWidth, texHeight);
			}

			mouseDragged(par1, par2, par3);

			int var6 = 14737632;


			if (!enabled)
				var6 = 10526880;
			else if (hovered)
				var6 = 16777120;

			drawCenteredString(var4, displayString, x + width / 2, y + (height - 8) / 2, var6);

		}
	}

	public void setDisplayItem(ItemStack par1ItemStack){
		blockToRender = null;
		itemToRender = null;

		if(par1ItemStack.getUnlocalizedName().startsWith("tile."))
			blockToRender = Block.getBlockFromItem(par1ItemStack.getItem());
		else
			itemToRender = par1ItemStack.getItem();

	}

	public Item getItemStack() {
		return (blockToRender != null ? Item.getItemFromBlock(blockToRender) : itemToRender);
	}

}
