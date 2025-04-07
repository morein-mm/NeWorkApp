package ru.netology.nmedia.activity

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.databinding.BottomSheetDialogBinding
import ru.netology.nmedia.enumeration.EventType
import ru.netology.nmedia.viewmodel.EventViewModel
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar


@AndroidEntryPoint
class BottomSheetDialogFragment : BottomSheetDialogFragment() {

    private val viewModel: EventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = BottomSheetDialogBinding.inflate(
            inflater,
            container,
            false
        )

        var dateTime = Calendar.getInstance()

        if (!viewModel.edited.value?.datetime.isNullOrEmpty()) {
            dateTime = Calendar.Builder()
                .setInstant(
                    ZonedDateTime
                        .parse(
                            viewModel.edited.value?.datetime,
                            itemDateTimePattern
                        )
                        .toEpochSecond() * 1000
                ).build()
            binding.dateEdit.setText(
                getDisplayDatetime(dateTime)
            )
        }

        binding.dateEdit.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                object : OnDateSetListener {
                    override fun onDateSet(
                        view: DatePicker?,
                        year: Int,
                        month: Int,
                        dayOfMonth: Int
                    ) {
                        dateTime.set(Calendar.YEAR, year);
                        dateTime.set(Calendar.MONTH, month);
                        dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        binding.dateEdit.setText(
                            getDisplayDatetime(dateTime)
                        )
                        println(getItemDatetime(dateTime))
                        viewModel.changeDatetime(
                            getItemDatetime(dateTime)
                        )
                        TimePickerDialog(
                            requireContext(),
                            object : OnTimeSetListener {
                                override fun onTimeSet(
                                    view: TimePicker?,
                                    hourOfDay: Int,
                                    minute: Int
                                ) {
                                    dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    dateTime.set(Calendar.MINUTE, minute);
                                    binding.dateEdit.setText(
                                        getDisplayDatetime(dateTime)
                                    )
                                    println(getItemDatetime(dateTime))
                                    viewModel.changeDatetime(
                                        getItemDatetime(dateTime)
                                    )
                                }
                            },
                            dateTime.get(Calendar.HOUR_OF_DAY),
                            dateTime.get(Calendar.MINUTE),
                            true
                        ).show()
                    }

                },
                dateTime.get(Calendar.YEAR),
                dateTime.get(Calendar.MONTH),
                dateTime.get(Calendar.DAY_OF_MONTH)
            ).show();
        }

        binding.eventTypeOnline.isChecked = viewModel.edited.value?.type == EventType.ONLINE
        binding.eventTypeOffline.isChecked = viewModel.edited.value?.type == EventType.OFFLINE

        binding.eventTypeGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.eventTypeOnline.id -> {
                    viewModel.changeType(EventType.ONLINE)
                }

                binding.eventTypeOffline.id -> {
                    viewModel.changeType(EventType.OFFLINE)
                }

                else -> {}
            }
        }

        return binding.root
    }

    companion object {

        val itemDateTimePattern: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")

        val displayDateTimePattern: DateTimeFormatter =
            DateTimeFormatter.ofPattern("MM/dd/yyyy' 'HH:mm")

        fun getDisplayDatetime(calendar: Calendar): String = calendar.time
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(displayDateTimePattern)

        fun getItemDatetime(calendar: Calendar): String = calendar.time
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .format(itemDateTimePattern)

    }
}