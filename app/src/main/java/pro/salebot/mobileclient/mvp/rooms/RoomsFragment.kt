package pro.salebot.mobileclient.mvp.rooms


import android.app.AlertDialog
import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.drawer_menu.*
import kotlinx.android.synthetic.main.fragment_rooms_new.*
import kotlinx.android.synthetic.main.view_content.*
import pro.salebot.mobileclient.Constants
import pro.salebot.mobileclient.R
import pro.salebot.mobileclient.database.DataBaseParams
import pro.salebot.mobileclient.models.Message
import pro.salebot.mobileclient.models.Project
import pro.salebot.mobileclient.models.Room
import pro.salebot.mobileclient.models.RoomsData
import pro.salebot.mobileclient.mvp.rooms.enums.RequestType
import pro.salebot.mobileclient.rv.rooms.RoomsAdapter
import pro.salebot.mobileclient.rv.rooms.RoomsAdapterListener
import pro.salebot.mobileclient.rv.rooms.menu.MenuAdapter
import pro.salebot.mobileclient.service.*
import pro.salebot.mobileclient.ui.activities.MainContract
import pro.salebot.mobileclient.utils.NotificationUtils
import pro.salebot.mobileclient.utils.dialogs.projectsbottomdialog.ProjectsBottomDialogFragment
import pro.salebot.mobileclient.utils.dialogs.projectsbottomdialog.ProjectsBottomDialogListener
import pro.salebot.mobileclient.utils.formatMenuCount
import pro.salebot.mobileclient.utils.isVisible
import pro.salebot.mobileclient.utils.log


class RoomsFragment : Fragment(), RoomsView, RoomsAdapterListener, ProjectsBottomDialogListener,
    ConnectServiceListener, MessagesUpdateListener {

    private var idProject: String = ""
    private var requestType: String = RequestType.ALL.name
    private var searchText: String = ""
    private var isProjectClick = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_rooms_new, container, false)!!


    private val roomsPresenterImpl = RoomsPresenterImpl(this)

    private var roomsAdapter = RoomsAdapter(mutableListOf(), this)
    private lateinit var menuAdapter: MenuAdapter
    private var projectList: List<Project> = emptyList()

    private var intentIdProject = Constants.DEFAULT_ID
    private var intentIdRoom = Constants.DEFAULT_ID

    private lateinit var login: String
    private lateinit var token: String
    private lateinit var dataBaseParams: DataBaseParams

    private var page = Constants.DEFAULT_PAGE

    private var messagesUpdateReceiver: MessagesUpdateReceiver? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        toolbarView.setNavigationIcon(R.drawable.ic_menu)
        (activity as AppCompatActivity).setSupportActionBar(toolbarView)
        toolbarView.setNavigationOnClickListener {
            showBottomSheetDialog()
//            drawerLayout.toggleMenu()
        }

        setHasOptionsMenu(true)

        logoutView.setOnClickListener {
            dataBaseParams.deleteKey(DataBaseParams.KEY_TOKEN)
            mainContract?.showLogin()
        }

//        navList.onItemClickListener = this

        swipeRefresh.setColorSchemeResources(
            R.color.colorPrimaryDark,
            R.color.colorPrimary
        )
        swipeRefresh.setOnRefreshListener {
            page = Constants.DEFAULT_PAGE
            roomsAdapter.loadmore = false
            roomsPresenterImpl.loadRooms(token, login, idProject, requestType, searchText)
        }
        swipeRefresh.isRefreshing = true

        dataBaseParams = DataBaseParams(context)
        login = dataBaseParams.getKey(DataBaseParams.KEY_LOGIN) ?: ""
        token = dataBaseParams.getKey(DataBaseParams.KEY_TOKEN) ?: ""

        roomsPresenterImpl.loadChannels(token, login)
        emailView.text = login

        errorView.visibility = View.GONE

        page = Constants.DEFAULT_PAGE

//        sToggleNotif.isChecked = "0" != dataBaseParams.getKey(DataBaseParams.KEY_RUN_NOTIF)
//        sToggleNotif.setOnCheckedChangeListener { _, isChecked ->
//            context?.let {
//                if (isChecked) {
//                    log("START THE FOREGROUND SERVICE ON DEMAND")
//                    actionOnService(it, Actions.START)
//                    dataBaseParams.setKey(DataBaseParams.KEY_RUN_NOTIF, 1)
//                } else {
//                    log("STOP THE FOREGROUND SERVICE ON DEMAND")
//                    actionOnService(it, Actions.STOP)
//                    dataBaseParams.setKey(DataBaseParams.KEY_RUN_NOTIF, 0)
//                }
//
//            }
//        }
//        initBottomSheetDialog()

        val clientClickListener: (View) -> Unit = {
            when (it.id) {
                R.id.tvAll -> rbmvAll.performClick()
                R.id.tvWait -> rbmvWait.performClick()
                R.id.tvMy -> rbmvMy.performClick()
                R.id.tvFree -> rbmvFree.performClick()
            }

        }

        tvAll.setOnClickListener(clientClickListener)
        tvWait.setOnClickListener(clientClickListener)
        tvMy.setOnClickListener(clientClickListener)
        tvFree.setOnClickListener(clientClickListener)

        rgMenu.setOnCheckedChangeListener { _, checkedId ->
            requestType = when (checkedId) {
                rbmvAll.id -> RequestType.ALL.name
                rbmvWait.id -> RequestType.WAIT.name
                rbmvMy.id -> RequestType.MY.name
                rbmvFree.id -> RequestType.FREE.name
                else -> throw IllegalStateException("This RequestType does not exist")
            }
            if (!isProjectClick) {
                swipeRefresh.isRefreshing = true
                errorView.isVisible = false
                roomsAdapter.clearAll()
                roomsPresenterImpl.loadRooms(token, login, idProject, requestType, searchText)
            }
        }
        showPushId()
        setTouchListenerForBottomMenu()
    }

    private fun setTouchListenerForBottomMenu() {
        val gestureDetector =
            GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent?,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    if (velocityX > 0) {
                        showBottomSheetDialog()
                    }
                    return true
                }
            })

        val touchListener = View.OnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
        }

        rbmvAll.setOnTouchListener(touchListener)
        rbmvWait.setOnTouchListener(touchListener)
        rbmvMy.setOnTouchListener(touchListener)
        rbmvFree.setOnTouchListener(touchListener)
        tvAll.setOnTouchListener(touchListener)
        tvWait.setOnTouchListener(touchListener)
        tvMy.setOnTouchListener(touchListener)
        tvFree.setOnTouchListener(touchListener)
    }

    override fun onNotifCheckedChangeListener(isChecked: Boolean) {
        context?.let {
            if (isChecked) {
                log("START THE FOREGROUND SERVICE ON DEMAND")
                actionOnService(it, Actions.START)
            } else {
                log("STOP THE FOREGROUND SERVICE ON DEMAND")
                actionOnService(it, Actions.STOP)
            }
        }
    }

    override fun onLogout() {
        dataBaseParams.deleteKey(DataBaseParams.KEY_TOKEN)
        mainContract?.showLogin()
        FirebaseMessaging.getInstance().deleteToken()
        NotificationUtils.cancelAll(requireContext())
    }

    private fun showBottomSheetDialog() {
        val projectsBottomDialogFragment: ProjectsBottomDialogFragment =
            ProjectsBottomDialogFragment.newInstance(login, projectList, this)
        projectsBottomDialogFragment.show(
            requireFragmentManager(),
            "ProjectsBottomDialogFragment.TAG"
        )
    }

//    private fun newNotif() {
//
//        var builder = NotificationCompat.Builder(context!!, "TestNotif111")
//            .setSmallIcon(R.drawable.ic_error)
//            .setContentTitle("textTitle")
//            .setContentText("textContent")
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
////            .setSound(Uri.parse("android.resource://" + context!!.packageName.toString() + "/" + R.raw.notification))
////            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE)
//
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = "channel_name111"
//            val descriptionText = "channel_description111"
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val attributes = AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                .build()
//            val sound =
//                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context!!.packageName + "/" + R.raw.notification)
//            val channel = NotificationChannel("TestNotif111", name, importance).apply {
//                description = descriptionText
//                setSound(sound, attributes)
////                defaults = Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE
//            }
//            // Register the channel with the system
//            val notificationManager: NotificationManager? =
//                context?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
//
//            notificationManager?.let { nm ->
//                val channelList = nm.notificationChannels
//                channelList?.forEach {
//                    nm.deleteNotificationChannel(it.id)
//                }
//            }
//            notificationManager?.createNotificationChannel(channel)
//        }
//        showPushId()
//    }

    private fun showPushId() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
//                etPushId.setText(task.exception?.localizedMessage)
                return@OnCompleteListener
            }
            val pushId = task.result
            roomsPresenterImpl.sendPushId(token, login, pushId!!)
        })
    }

    private var menuToolbar: Menu? = null

    private fun getSearchView() = menuToolbar?.findItem(R.id.app_bar_search).let {
        it?.actionView as SearchView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_toolbar_room, menu)
        menuToolbar = menu
        val mSearchView = getSearchView()
        mSearchView.queryHint = getString(R.string.rooms_toolbar_search)
        val searchEditFrame: LinearLayout =
            mSearchView.findViewById(R.id.search_edit_frame) as LinearLayout // Get the Linear Layout
        (searchEditFrame.layoutParams as LinearLayout.LayoutParams).leftMargin = 0
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchText = it
                    if (!isProjectClick) {
                        swipeRefresh.isRefreshing = true
                        roomsPresenterImpl.loadRooms(
                            token,
                            login,
                            idProject,
                            requestType,
                            searchText
                        )
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
        mSearchView.setOnCloseListener {
            searchText = ""
            if (!isProjectClick) {
                swipeRefresh.isRefreshing = true
                roomsPresenterImpl.loadRooms(token, login, idProject, requestType, searchText)
            }
            false
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun updateActionMenu() {
        if (dataBaseParams.getKey(DataBaseParams.KEY_ID_NOTIFICATION + idProject).isNullOrEmpty()) {
            toolbarView.menu.findItem(R.id.action_notification)?.title =
                getString(R.string.off_notification)
        } else {
            toolbarView.menu.findItem(R.id.action_notification)?.title =
                getString(R.string.on_notification)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        item?.let {
            when (it.itemId) {
                R.id.action_notification -> {
                    if (dataBaseParams.getKey(DataBaseParams.KEY_ID_NOTIFICATION + idProject)
                            .isNullOrEmpty()
                    ) {
                        roomsPresenterImpl.subscribeNotification(
                            false,
                            token,
                            login,
                            idProject
                        )
                        it.title = getString(R.string.on_notification)
                        dataBaseParams.setKey(DataBaseParams.KEY_ID_NOTIFICATION + idProject, "1")
                    } else {
                        roomsPresenterImpl.subscribeNotification(
                            true,
                            token,
                            login,
                            idProject
                        )
                        it.title = getString(R.string.off_notification)
                        dataBaseParams.deleteKey(DataBaseParams.KEY_ID_NOTIFICATION + idProject)
                    }
                    menuAdapter.notifyDataSetChanged()
                }
            }
        }

        return true
    }

    private var handlerService = Handler()
    private var runService = Runnable {
        ConnectForAppService.startWithListener(context!!, login, token, this)
        log("START THE FOREGROUND SERVICE ON DEMAND")
        context?.let {
            val isRunService = "0" != dataBaseParams.getKey(DataBaseParams.KEY_RUN_NOTIF)
            if (isRunService) {
                actionOnService(it, Actions.START)
            }
        }
    }

    private fun actionOnService(context: Context, action: Actions) {
//        if (getServiceState(context) == ServiceState.STOPPED && action == Actions.STOP) return
//        Intent(context, EndlessService::class.java).also {
//            it.action = action.name
//            ContextCompat.startForegroundService(context, it)
//            log("Starting service")
//        }
    }

    override fun onStart() {
        super.onStart()
        val dataBaseParams = DataBaseParams(context)
        val login: String = dataBaseParams.getKey(DataBaseParams.KEY_LOGIN) ?: ""
        val token: String = dataBaseParams.getKey(DataBaseParams.KEY_TOKEN) ?: ""
        if (!login.isEmpty()) {
            if (!token.isEmpty()) {
                handlerService.postDelayed(runService, 1000)
                setReceiverUpdateMessage()
            }
        }
        if (!idProject.isEmpty()) {
            roomsPresenterImpl.loadUpdate(token, login, idProject, requestType, "", page)
        }
        context?.let { context ->
            roomsAdapter.roomsList.forEach {
                NotificationUtils.cancel(context, it.id.toInt())
            }
        }
    }

    private fun setReceiverUpdateMessage() {
        messagesUpdateReceiver = MessagesUpdateReceiver(this)
        val filter = IntentFilter();
        filter.addAction(ACTION_UPDATE_MESSAGES);
        activity?.registerReceiver(messagesUpdateReceiver, filter)
    }

    override fun onNewMessageUpdate(message: Message) {
        roomsAdapter.loadmore = false
        roomsPresenterImpl.loadUpdate(token, login, idProject, requestType, searchText, page)
        Handler(Looper.getMainLooper()).postDelayed({
            context?.let { context ->
                roomsAdapter.roomsList.forEach {
                    NotificationUtils.cancel(context, it.id.toInt())
                }
            }
        }, 1000)
    }

    override fun onNewMessageService(message: Message) {
        roomsAdapter.loadmore = false
        roomsPresenterImpl.loadUpdate(token, login, idProject, requestType, searchText, page)
        Handler(Looper.getMainLooper()).postDelayed({
            context?.let { context ->
                roomsAdapter.roomsList.forEach {
                    NotificationUtils.cancel(context, it.id.toInt())
                }
            }
        }, 1000)

    }

    override fun onStop() {
        super.onStop()
        handlerService.removeCallbacks(runService)
        try {
            ConnectForAppService.stop(context)
            activity?.unregisterReceiver(messagesUpdateReceiver)
        } catch (e: IllegalStateException) {
        }
    }


    override fun onSuccess(roomsData: RoomsData) {
        val roomsList = roomsData.clients.toMutableList()

        rbmvAll.text = formatMenuCount(roomsData.all)
        rbmvWait.text = formatMenuCount(roomsData.wait)
        rbmvMy.text = formatMenuCount(roomsData.my)
        rbmvFree.text = formatMenuCount(roomsData.free)

        val isSingleClient = roomsData.usersCount == 1

        rbmvMy.isVisible = !isSingleClient
        rbmvFree.isVisible = !isSingleClient

        tvMy.isVisible = !isSingleClient
        tvFree.isVisible = !isSingleClient

        if (roomsData.clients.isNotEmpty()) {
            isLoadMoreRun = false
        }
        try {
            errorView.visibility = View.GONE
            if (roomsList.isEmpty() && roomsAdapter.roomsList.isEmpty()) {
                roomsAdapter.clearAll()
                onFailed(getString(R.string.empty_list))
                return
            }

            val countRequestType = when (requestType) {
                RequestType.ALL.name -> roomsData.all
                RequestType.WAIT.name -> roomsData.wait
                RequestType.FREE.name -> roomsData.free
                RequestType.MY.name -> roomsData.my
                else -> 0
            }

            if (swipeRefresh.isRefreshing) {
//                roomsAdapter.loadmore = true
                if (roomsAdapter.roomsList.isEmpty()) {
                    roomsAdapter = RoomsAdapter(roomsList, this)
                    recyclerView.adapter = roomsAdapter
                } else {
                    roomsAdapter.update(roomsList)
                }
            } else if (roomsAdapter.loadmore) {
                roomsAdapter.roomsList.addAll(roomsList)
                roomsAdapter.notifyDataSetChanged()
            } else {
                roomsAdapter.update(roomsList)
            }

            roomsAdapter.loadMore(roomsAdapter.roomsList.size < countRequestType)
            swipeRefresh.isRefreshing = false

            if (intentIdRoom != Constants.DEFAULT_ID) {
                roomsList.forEach {
                    if (it.id == intentIdRoom.toString()) {
                        mainContract?.showMessages(
                            idProject,
                            it.id,
                            it.name,
                            it.avatar,
                            it.client_type
                        )
                    }
                }
                intentIdRoom = Constants.DEFAULT_ID
            }
        } catch (e: java.lang.IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun onFailed(errorMess: String) {
        if (roomsAdapter.roomsList.size == 0) {
            errorView.visibility = View.VISIBLE
            errorViewText.text = errorMess
            if (errorMess.contains("Use JsonReader", true)) {
                dataBaseParams.deleteKey(DataBaseParams.KEY_TOKEN)
                mainContract?.showLogin()
            }
        }
        recyclerView.adapter = roomsAdapter
        swipeRefresh.isRefreshing = false
        Handler().post {
            roomsAdapter.loadMore(false)
        }
    }

    override fun onItemClick(room: Room) {
        mainContract?.showMessages(idProject, room.id, room.name, room.avatar, room.client_type)
    }

    override fun onLoadmore() {
        if (!isLoadMoreRun) {
            isLoadMoreRun = true
            roomsPresenterImpl.loadMore(token, login, idProject, requestType, searchText, ++page)
        }
    }

    override fun onSuccessMenu(menuItems: List<Project>) {
        projectList = menuItems
        menuAdapter = MenuAdapter(context, menuItems)
        navList.adapter = menuAdapter

        var idProjectDB = Constants.DEFAULT_ID.toString()
        if (!menuItems.isNullOrEmpty()) {
            idProjectDB = if (intentIdProject == Constants.DEFAULT_ID)
                dataBaseParams.getKey(DataBaseParams.KEY_ID_PROJECT)
                    ?: menuItems[0].id else intentIdProject.toString()
        }
        intentIdProject = Constants.DEFAULT_ID

        menuItems.forEachIndexed { index, menu ->
            if (menu.id == idProjectDB) {
                idProject = menuItems[index].id
                toolbarView.title = menuItems[index].name
                menuAdapter.updateCheck(index)
                roomsPresenterImpl.loadRooms(token, login, idProject, requestType, searchText)
                updateActionMenu()
            }
        }
    }

    override fun onProjectClick(pos: Int) {
        requestType = RequestType.ALL.name
        isProjectClick = true
        getSearchView().onActionViewCollapsed()

        swipeRefresh.isRefreshing = true

        rbmvAll.isChecked = true

        roomsAdapter.loadmore = false

        roomsAdapter.clearAll()

        val project = menuAdapter.getItem(pos)
        menuAdapter.updateCheck(pos)
        idProject = project.id
        toolbarView.title = project.name
//        drawerLayout.toggleMenu()
        roomsPresenterImpl.loadRooms(token, login, idProject, requestType, searchText)

        dataBaseParams.setKey(DataBaseParams.KEY_ID_PROJECT, idProject)

        updateActionMenu()

        isProjectClick = false
    }

//    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//        swipeRefresh.isRefreshing = true
//
//        roomsAdapter.loadmore = false
//
//        roomsAdapter.clearAll()
//
//        val project = menuAdapter.getItem(p2)
//        menuAdapter.updateCheck(p2)
//        idProject = project.id
//        toolbarView.title = project.name
//        drawerLayout.toggleMenu()
//        roomsPresenterImpl.loadRooms(token, login, idProject)
//
//        dataBaseParams.setKey(DataBaseParams.KEY_ID_PROJECT, idProject)
//
//        updateActionMenu()
//    }

    override fun onItemLongClick(index: Int) {
        AlertDialog.Builder(context, R.style.AlertDialogStyle)
            .setTitle(getString(R.string.delete_dialog))
            .setPositiveButton(R.string.yes) { _, _ ->
                roomsPresenterImpl.deleteRoom(
                    index,
                    token,
                    login,
                    roomsAdapter.roomsList[index].id,
                    idProject
                )
            }
            .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onUpdateRoom(roomsList: RoomsData) {
        errorView.visibility = View.GONE
        roomsAdapter.update(roomsList.clients.toMutableList())
    }

    override fun onSuccessDeleteRoom(index: Int) {
        roomsAdapter.delete(index)
    }

    override fun onFailedDeleteRoom(errorMess: String) {
        Toast.makeText(context, errorMess, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        roomsPresenterImpl.cancel()
    }

    private var mainContract: MainContract? = null

    companion object {
        var isLoadMoreRun = false

        @JvmStatic
        fun newInstance(mainContract: MainContract) = RoomsFragment()
            .apply {
                this.mainContract = mainContract
            }

        fun newInstance(mainContract: MainContract, idProject: Int, idRoom: Int) =
            RoomsFragment().apply {
                intentIdProject = idProject
                intentIdRoom = idRoom
                this.mainContract = mainContract
            }
    }

}