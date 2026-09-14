package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.LaboratoryResult
import com.example.data.local.entity.Sample
import com.example.ui.theme.LabCriticalOnRed
import com.example.ui.theme.LabCriticalRed
import com.example.ui.theme.LabCriticalRedContainer
import com.example.ui.theme.LabNormalGreen
import com.example.ui.theme.LabNormalGreenContainer
import com.example.ui.theme.LabTealContainer
import com.example.ui.theme.LabTealOnContainer
import com.example.ui.theme.LabTealPrimary
import com.example.ui.theme.LabWarningContainer
import com.example.ui.theme.LabWarningOrange

@Composable
fun SampleStatusBadge(status: String) {
    val (label, bgColor, textColor, icon) = when (status) {
        Sample.STATUS_PENDING -> Quadruple(
            "معلقة (Pending)",
            LabWarningContainer,
            LabWarningOrange,
            Icons.Default.HourglassTop
        )
        Sample.STATUS_RECEIVED -> Quadruple(
            "مستلمة (Received)",
            Color(0xFFE0F2FE),
            Color(0xFF0369A1),
            Icons.Default.Info
        )
        Sample.STATUS_IN_ANALYSIS -> Quadruple(
            "قيد التحليل (Analyzing)",
            LabTealContainer,
            LabTealOnContainer,
            Icons.Default.Info
        )
        Sample.STATUS_COMPLETED -> Quadruple(
            "مكتملة (Completed)",
            LabNormalGreenContainer,
            LabNormalGreen,
            Icons.Default.CheckCircle
        )
        Sample.STATUS_REJECTED -> Quadruple(
            "مرفوضة (Rejected)",
            LabCriticalRedContainer,
            LabCriticalRed,
            Icons.Default.Warning
        )
        else -> Quadruple(
            status,
            Color(0xFFF1F5F9),
            Color(0xFF475569),
            Icons.Default.Info
        )
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = textColor, modifier = Modifier.size(12.dp))
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ResultStatusBadge(status: String) {
    val (label, bgColor, textColor) = when (status) {
        LaboratoryResult.STATUS_NORMAL -> Triple("طبيعي (Normal)", LabNormalGreenContainer, LabNormalGreen)
        LaboratoryResult.STATUS_ABNORMAL -> Triple("غير طبيعي (Abnormal)", LabWarningContainer, LabWarningOrange)
        LaboratoryResult.STATUS_CRITICAL -> Triple("حرج (CRITICAL)", LabCriticalRedContainer, LabCriticalRed)
        LaboratoryResult.STATUS_PENDING -> Triple("معلق (Pending)", Color(0xFFF1F5F9), Color(0xFF64748B))
        else -> Triple(status, Color(0xFFF1F5F9), Color(0xFF64748B))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StatSummaryCard(
    title: String,
    count: Int,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    color = contentColor.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$count",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = contentColor.copy(alpha = 0.75f)
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(contentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    title: String,
    description: String,
    icon: ImageVector,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            if (actionText != null && onAction != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onAction,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = actionText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ConfirmDeleteDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = LabCriticalRed
            )
        },
        text = {
            Text(
                text = message,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = LabCriticalRed),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("تأكيد الحذف", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("إلغاء")
            }
        }
    )
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
