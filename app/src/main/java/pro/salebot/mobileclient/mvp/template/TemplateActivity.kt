package pro.salebot.mobileclient.mvp.template

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_template.*
import pro.salebot.mobileclient.R
import pro.salebot.mobileclient.database.DataBaseParams
import pro.salebot.mobileclient.mvp.template.adapter.TemplateAdapter
import pro.salebot.mobileclient.mvp.template.entity.TemplateItem
import pro.salebot.mobileclient.utils.isVisible

class TemplateActivity : AppCompatActivity(), TemplateView {

    private lateinit var presenter: TemplatePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template)

        toolbarView.setNavigationIcon(R.drawable.ic_back)

        rvTemplate.adapter = TemplateAdapter {
            presenter.onChipClicked(it.id, it.answer)
        }

        presenter = TemplatePresenterImpl(
            TemplateViewModel(
                intent.getStringExtra(EXTRA_PROJECT_ID)!!
            ),
            this,
            TemplateModelImpl(
                DataBaseParams(this)
            )
        )

        initListeners()

        presenter.onCreate()
    }

    private fun initListeners() {
        toolbarView.setNavigationOnClickListener {
            finish()
        }
        rgColors.setOnCheckedChangeListener { _, checkedId ->
            val messageType = when (checkedId) {
                R.id.rbColor1 -> 0
                R.id.rbColor2 -> 1
                R.id.rbColor3 -> 2
                R.id.rbColor4 -> 3
                R.id.rbColor5 -> 4
                R.id.rbColor6 -> 5
                R.id.rbColor7 -> 6
                R.id.rbColor8 -> 7
                else -> null
            }
            presenter.onColorCheckedChange(messageType)
        }
        ivSearch.setOnClickListener {
            presenter.onSearchClick(etSearch.text.toString())
        }
        btnShowAll.setOnClickListener {
            presenter.onShowAllClick()
        }
    }

    override fun clearColorsCheck() {
        rgColors.clearCheck()
    }

    override fun changeShowAllVisibility(isVisible: Boolean) {
        btnShowAll.isVisible = isVisible
    }

    override fun addTemplateList(templates: List<TemplateItem>) {
        with(rvTemplate.adapter as TemplateAdapter) {
            list = templates
            notifyDataSetChanged()
        }
    }

    override fun goToBackWithResult(id: Int, answer: String) {
        setResult(
            RESULT_OK,
            Intent()
                .putExtra(EXTRA_ANSWER_ID_RESULT, id)
                .putExtra(EXTRA_ANSWER_TEXT_RESULT, answer)
        )
        finish()
    }

    override fun showLoading(isVisible: Boolean) {
        pbLoading.isVisible = isVisible
        rvTemplate.isVisible = !isVisible
        rgColors.isEnabled = !isVisible
        ivSearch.isEnabled = !isVisible
    }

    override fun showFailureMessage() {
        Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_ANSWER_TEXT_RESULT = "EXTRA_ANSWER_TEXT_RESULT"
        const val EXTRA_ANSWER_ID_RESULT = "EXTRA_ANSWER_RESULT"
        private const val EXTRA_PROJECT_ID = "EXTRA_PROJECT_ID"

        fun getIntent(context: Context, projectId: String) =
            Intent(context, TemplateActivity::class.java)
                .putExtra(EXTRA_PROJECT_ID, projectId)
    }
}