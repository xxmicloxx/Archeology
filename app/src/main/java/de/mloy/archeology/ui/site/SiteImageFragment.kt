package de.mloy.archeology.ui.site

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import de.mloy.archeology.R
import de.mloy.archeology.getViewModel
import de.mloy.archeology.helper.createImagePickerIntent

class SiteImageFragment : Fragment() {
    private var image: String? = null
    private var index: Int? = null
    private lateinit var viewModel: SiteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            image = it.getString(ARG_IMAGE)
            index = it.getInt(ARG_INDEX)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = requireActivity().getViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_site_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val imageView = view.findViewById<ImageView>(R.id.siteImage)
        val addLayout = view.findViewById<View>(R.id.addLayout)

        if (image != null) {
            imageView.visibility = View.VISIBLE
            addLayout.visibility = View.GONE

            Glide.with(this).load(image).into(imageView)
            imageView.setOnClickListener {
                it.showContextMenu()
            }
            imageView.setOnLongClickListener {
                it.showContextMenu()
            }
            registerForContextMenu(imageView)
        } else {
            imageView.visibility = View.GONE
            addLayout.visibility = View.VISIBLE

            addLayout.setOnClickListener {
                val intent = createImagePickerIntent(context!!)
                startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
            }
            unregisterForContextMenu(imageView)
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        requireActivity().menuInflater.inflate(R.menu.site_image_options, menu)
        menu.setHeaderTitle(R.string.site_image_options)

        menu.findItem(R.id.remove).setOnMenuItemClickListener {
            val idx = index
            if (idx != null) {
                viewModel.removeImage(idx)
            }
            true
        }

        menu.findItem(R.id.view).setOnMenuItemClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(image), "image/*")
            // prevent the other app being shown in our recent apps entry, this would confuse users
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PICK_IMAGE_REQUEST_CODE -> {
                val imageUrl = data?.dataString ?: return
                viewModel.addImage(imageUrl)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {

        const val ARG_IMAGE = "image"
        const val ARG_INDEX = "index"
        private const val PICK_IMAGE_REQUEST_CODE = 0

        @JvmStatic
        fun newInstance(image: String?, index: Int?) =
            SiteImageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_IMAGE, image)
                    if (index != null) {
                        putInt(ARG_INDEX, index)
                    }
                }
            }
    }
}
