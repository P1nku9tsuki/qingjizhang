package com.natsuki.qingjizhang

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun AgreementScreen(
    onAgree: () -> Unit,
    onReject: () -> Unit
) {
    val primaryColor: Color = MiuixTheme.colorScheme.primary
    val textPrimary: Color = MiuixTheme.colorScheme.onSurface
    val textSecondary: Color = MiuixTheme.colorScheme.onSurface
    val onPrimaryColor = if (primaryColor.luminance() > 0.5f) Color(0xFF1A1A1A) else Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MiuixTheme.colorScheme.surface)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Text(
            "用户协议与隐私政策",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "请仔细阅读以下条款，同意后方可使用轻记账",
            fontSize = 13.sp,
            color = textSecondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MiuixTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AgreementSection(
                    title = "一、服务说明",
                    body = "1. 轻记账是一款以本地存储为核心的个人记账工具。您的账单数据默认仅保存在设备本地，不会主动上传至任何服务器。\n\n" +
                            "2. 本应用不提供账号注册或云端同步服务。\n\n" +
                            "3. 为避免数据丢失，建议您定期使用「设置 → 导出 JSON」功能备份账单。因设备损坏、卸载、系统清理等导致的数据丢失，本应用不承担责任。"
                )

                AgreementSection(
                    title = "二、权限说明",
                    body = "为保障功能正常运行，本应用可能申请以下权限：\n\n" +
                            "· 振动权限：用于界面触觉反馈；\n\n" +
                            "· 网络权限：仅用于「检查更新」功能访问 GitHub Releases 接口，不传输任何账单数据；\n\n" +
                            "· 存储 / 文件读写：仅在您主动使用「导入 / 导出」功能时触发，由您自主选择文件位置。\n\n" +
                            "以上权限均为可选，拒绝授权不影响核心记账功能的使用。"
                )

                AgreementSection(
                    title = "三、隐私保护",
                    body = "1. 我们不会收集、上传、分享您的任何个人信息。\n\n" +
                            "2. 账单数据、分类设置、主题偏好、学习记录等全部存储于设备本地。\n\n" +
                            "3. 唯一的外部网络请求为「检查更新」：向 GitHub Releases API 发起版本比对请求。该请求不包含任何个人数据或账单内容，如不希望产生请求，可在「设置 → 关于」中关闭「自动检查更新」。\n\n" +
                            "4. 本应用不含任何第三方广告、统计或数据采集 SDK。"
                )

                AgreementSection(
                    title = "四、用户责任",
                    body = "1. 您应妥善保管设备与备份文件，防止数据泄露。\n\n" +
                            "2. 您应遵守所在国家或地区的法律法规，不得将本应用用于任何违法用途。\n\n" +
                            "3. 本应用提供的统计、预算等功能仅供参考，不构成任何财务、投资或税务建议。"
                )

                AgreementSection(
                    title = "五、知识产权",
                    body = "1. 本应用的界面设计、图标、文案等原创内容，其著作权归开发者所有。\n\n" +
                            "2. 本应用所依赖的开源项目，其版权归各自作者所有，具体清单与协议可在「设置 → 开源致谢」中查看。\n\n" +
                            "3. 本应用源代码以 GPL-3.0 协议开源，您可以自由学习、修改和分发，但需遵守该协议的相关条款。"
                )

                AgreementSection(
                    title = "六、免责声明",
                    body = "1. 本应用按「现状」提供，不附带任何形式的明示或暗示担保。\n\n" +
                            "2. 在法律允许的最大范围内，开发者不对因使用或无法使用本应用而产生的任何直接或间接损失负责。\n\n" +
                            "3. 若您不同意本协议的任何条款，请立即停止使用并卸载本应用。"
                )

                AgreementSection(
                    title = "七、未成年人保护",
                    body = "1. 本应用面向普通用户。若您是未满 14 周岁的未成年人，请在监护人陪同下阅读本协议，并在监护人同意后使用本应用。\n\n" +
                            "2. 本应用不会主动收集任何未成年人的个人信息。"
                )

                AgreementSection(
                    title = "八、协议修改",
                    body = "1. 我们保留随时修改本协议的权利，修改后的协议将在应用内以弹窗形式重新提示。\n\n" +
                            "2. 若您在协议更新后继续使用本应用，即视为接受修改后的条款。\n\n" +
                            "3. 若您不接受修改后的条款，请停止使用并卸载本应用。"
                )

                AgreementSection(
                    title = "九、法律适用与争议解决",
                    body = "1. 本协议的订立、执行与解释均适用中华人民共和国法律。\n\n" +
                            "2. 因本协议产生的争议，双方应优先协商解决；协商不成的，可向开发者所在地有管辖权的人民法院提起诉讼。"
                )

                AgreementSection(
                    title = "十、联系我们",
                    body = "如对本协议或隐私保护有任何疑问，您可通过 GitHub 仓库的 Issues 与我们联系。\n\n" +
                            "感谢您使用轻记账。"
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "最后更新：2026 年 9 月",
                    fontSize = 12.sp,
                    color = textSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onAgree,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor,
                contentColor = onPrimaryColor
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("同意并继续", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = onReject,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("不同意", color = textSecondary, fontSize = 14.sp)
        }
    }
}

@Composable
private fun AgreementSection(
    title: String,
    body: String
) {
    val textPrimary: Color = MiuixTheme.colorScheme.onSurface
    val textSecondary: Color = MiuixTheme.colorScheme.onSurface

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
        Text(
            title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            body,
            fontSize = 13.sp,
            color = textSecondary,
            lineHeight = 20.sp
        )
    }
}

private fun Color.luminance(): Float {
    return 0.299f * red + 0.587f * green + 0.114f * blue
}