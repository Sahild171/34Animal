package com.application.animalAlertApp.Adapters

import android.content.Context
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.application.animalAlertApp.Interfaces.CardInterface

import com.application.animalAlertApp.R
import com.application.animalAlertApp.data.Response.Payment.FindCard

class CardPagerAdapter(val context: Context, var list: List<FindCard>,val cardInterface: CardInterface) :
    RecyclerView.Adapter<CardPagerAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(parent)
    }


    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        // holder.imageView.setImageResource(list.get(position).image)
        holder.tv_cardnumber.setText(list[position].number)
        holder.tv_validupto.setText(list[position].exp_month + " / " + list[position].exp_year)
        holder.tv_cvv.setText(list[position].cvc)

        holder.dots.setOnClickListener {
            showpopmenu(holder,list[position])
        }

    }

    fun setItem(list: List<FindCard>) {
        this.list = list
        notifyDataSetChanged()
    }

    private fun showpopmenu(holder: CardViewHolder,card:FindCard) {
        val popup = PopupMenu(context, holder.dots, Gravity.END, 0, R.style.MyPopupMenu)
        popup.inflate(R.menu.pop_menu_card)
        popup.show()

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.edit -> {
                    cardInterface.onCardEdit(card,holder.layoutPosition)
                }
//                R.id.setdef -> {
//                    cardInterface.onSetasDefault(card._id)
//                }
//                R.id.delete -> {
//                    cardInterface.onDeleteCard(card._id,holder.layoutPosition)
//                }
            }
            true
        })

    }

    override fun getItemCount(): Int = list.size

    class CardViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        constructor(parent: ViewGroup) : this(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_item,
                parent, false))

        val imageView = itemView.findViewById<View>(R.id.img_logo) as ImageView
        val dots = itemView.findViewById<View>(R.id.img_dots) as ImageView
        val tv_cardnumber = itemView.findViewById<View>(R.id.tv_cardnumber) as TextView
        val tv_validupto = itemView.findViewById<View>(R.id.tv_validupto) as TextView
        val tv_cvv = itemView.findViewById<View>(R.id.tv_cvv) as TextView
    }
}