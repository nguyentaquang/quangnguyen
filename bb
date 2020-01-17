/*============================================================================*/
/* Project      = AUTOSAR Renesas X2x MCAL Components                         */
/* Module       = Lin_RLIN3_Irq.c                                             */
/* Version      = 1.0.0                                                       */
/* Date         = 03-Dec-2019                                                 */
/*============================================================================*/
/*                                  COPYRIGHT                                 */
/*============================================================================*/
/* (c) 2019 Renesas Electronics Corporation. All rights reserved.             */
/*============================================================================*/
/* Purpose:                                                                   */
/* ISR functions of the LIN Driver Component                                  */
/*                                                                            */
/*============================================================================*/
/*                                                                            */
/* Unless otherwise agreed upon in writing between your company and           */
/* Renesas Electronics Corporation the following shall apply!                 */
/*                                                                            */
/* Warranty Disclaimer                                                        */
/*                                                                            */
/* There is no warranty of any kind whatsoever granted by Renesas. Any        */
/* warranty is expressly disclaimed and excluded by Renesas, either expressed */
/* or implied, including but not limited to those for non-infringement of     */
/* intellectual property, merchantability and/or fitness for the particular   */
/* purpose.                                                                   */
/*                                                                            */
/* Renesas shall not have any obligation to maintain, service or provide bug  */
/* fixes for the supplied Product(s) and/or the Application.                  */
/*                                                                            */
/* Each User is solely responsible for determining the appropriateness of     */
/* using the Product(s) and assumes all risks associated with its exercise    */
/* of rights under this Agreement, including, but not limited to the risks    */
/* and costs of program errors, compliance with applicable laws, damage to    */
/* or loss of data, programs or equipment, and unavailability or              */
/* interruption of operations.                                                */
/*                                                                            */
/* Limitation of Liability                                                    */
/*                                                                            */
/* In no event shall Renesas be liable to the User for any incidental,        */
/* consequential, indirect, or punitive damage (including but not limited     */
/* to lost profits) regardless of whether such liability is based on breach   */
/* of contract, tort, strict liability, breach of warranties, failure of      */
/* essential purpose or otherwise and even if advised of the possibility of   */
/* such damages. Renesas shall not be liable for any services or products     */
/* provided by third party vendors, developers or consultants identified or   */
/* referred to the User by Renesas in connection with the Product(s) and/or   */
/* the Application.                                                           */
/*                                                                            */
/*============================================================================*/
/* Environment:                                                               */
/*              Devices:        X2x                                           */
/*============================================================================*/

/*******************************************************************************
**                      Revision Control History                              **
*******************************************************************************/
/*
 * 1.0.0:  03-Dec-2019   : Initial Version
 */
/******************************************************************************/

/******************************************************************************/

/*******************************************************************************
**                      Include Section                                       **
*******************************************************************************/
/* Lin Transmit receive header for the target */
#include "Lin_RLIN3_LLDriver.h"
/* Lin APIs header file */
#include "Lin.h"
#if (LIN_DEV_ERROR_DETECT == STD_ON)
/* Default Error header */
#include "Det.h"
#endif
/* Included for declaration of the function Dem_ReportErrorStatus() */
#include "Dem.h"
#if (LIN_WAKEUP_SUPPORT == STD_ON)
/* Included for the declaration of EcuM_CheckWakeup() */
/* EcuM Callback header file */
#include "EcuM_Cbk.h"
#endif
/* Schedule Manager header */
#if (LIN_CRITICAL_SECTION_PROTECTION == STD_ON)
#include "SchM_Lin.h"
#endif
/* Provision for platform dependent types */
#include "rh850_Types.h"

/*******************************************************************************
**                      Version Information                                   **
*******************************************************************************/
/* AUTOSAR Release version information */
#define LIN_RLIN3_LLDRIVER_C_AR_RELEASE_MAJOR_VERSION  \
                                         LIN_AR_RELEASE_MAJOR_VERSION_VALUE
#define LIN_RLIN3_LLDRIVER_C_AR_RELEASE_MINOR_VERSION   \
                                         LIN_AR_RELEASE_MINOR_VERSION_VALUE
#define LIN_RLIN3_LLDRIVER_C_AR_RELEASE_REVISION_VERSION  \
                                         LIN_AR_RELEASE_REVISION_VERSION_VALUE

/* File version information */
#define LIN_RLIN3_LLDRIVER_C_SW_MAJOR_VERSION    LIN_SW_MAJOR_VERSION_VALUE
#define LIN_RLIN3_LLDRIVER_C_SW_MINOR_VERSION    LIN_SW_MINOR_VERSION_VALUE

/*******************************************************************************
**                      Version Check                                         **
*******************************************************************************/
#if (LIN_RLIN3_LLDRIVER_AR_RELEASE_MAJOR_VERSION !=  \
                                  LIN_RLIN3_LLDRIVER_C_AR_RELEASE_MAJOR_VERSION)
  #error "Lin_RLIN3_LLDriver.c : Mismatch in Release Major Version"
#endif

#if (LIN_RLIN3_LLDRIVER_AR_RELEASE_MINOR_VERSION !=  \
                                  LIN_RLIN3_LLDRIVER_C_AR_RELEASE_MINOR_VERSION)
  #error "Lin_RLIN3_LLDriver.c : Mismatch in Release Minor Version"
#endif

#if (LIN_RLIN3_LLDRIVER_AR_RELEASE_REVISION_VERSION != \
                               LIN_RLIN3_LLDRIVER_C_AR_RELEASE_REVISION_VERSION)
  #error "Lin_RLIN3_LLDriver.c : Mismatch in Release Revision Version"
#endif

#if (LIN_RLIN3_LLDRIVER_SW_MAJOR_VERSION !=  \
                                LIN_RLIN3_LLDRIVER_C_SW_MAJOR_VERSION)
  #error "Lin_RLIN3_LLDriver.c : Mismatch in Software Major Version"
#endif

#if (LIN_RLIN3_LLDRIVER_SW_MINOR_VERSION !=  \
                                LIN_RLIN3_LLDRIVER_C_SW_MINOR_VERSION)
  #error "Lin_RLIN3_LLDriver.c : Mismatch in Software Minor Version"
#endif

/*******************************************************************************
**                      MISRA C Rule Violations                               **
*******************************************************************************/
/* 1. MISRA C RULE VIOLATION:                                                 */
/* Message       : (4:0489) Increment or decrement operation performed on     */
/*               : pointer.                                                   */
/* Rule          : MISRA-C:2004 Rule 17.4                                     */
/* Justification : To have a better readability of the code which will help   */
/*                 for debugging purpose array indexing is not performed.     */
/* Verification  : However, part of the code is verified manually and it is   */
/*                 not having any impact.                                     */
/* Reference     : Look for START Msg(4:0489)-1 and                           */
/*                 END Msg(4:0489)-1 tags in the code.                        */
/******************************************************************************/
/* 2. MISRA C RULE VIOLATION:                                                 */
/* Message       : (4:0491) Array subscripting applied to an object of        */
/*                 pointer type.                                              */
/* Rule          : MISRA-C:2004 Rule 17.4                                     */
/* Justification : Subscripting cannot be applied on the array since size can */
/*                 grow based on configuration done by user i.e. multi        */
/*                 configuration                                              */
/* Verification  : However, part of the code is verified manually and it is   */
/*                 not having any impact.                                     */
/* Reference     : Look for START Msg(4:0491)-2 and                           */
/*                 END Msg(4:0491)-2 tags in the code.                        */
/******************************************************************************/
/* 3. MISRA C RULE VIOLATION:                                                */
/* Message       : (4:3415)Right hand operand of '&&' or '||' is an           */
/*                 expression with possible side effects.                     */
/* Rule          : MISRA-C:2004 Rule 12.4                                     */
/* Justification : The left hand operand of '&&' is not always false and left */
/*                 hand operand of '||' is not always true.                   */
/* Verification  : However, part of the code is verified manually and it is   */
/*                 not having any impact.                                     */
/* Reference     : Look for START Msg(4:3415)-3 and                           */
/*                 END Msg(4:3415)-3 tags in the code.                        */
/******************************************************************************/
/* 4. MISRA C RULE VIOLATION:                                                 */
/* Message       : (4:2982) This assignment is redundant. The value           */
/*                 of this object is never used before being modified.        */
/* Justification : Typecasting from void* is necessary to hide internal types */
/*                 from the header files which are exposed to user.           */
/* Verification  : However, part of the code is verified manually and it is   */
/*                 not having any impact.                                     */
/* Reference     : Look for START Msg(4:2982)-4 and                           */
/*                 END Msg(4:2982)-4 tags in the code.                        */
/******************************************************************************/
/* 5. MISRA C RULE VIOLATION:                                                 */
/* Message       : (4:2985) This assignment is redundant. The value           */
/*                 of this object is never used before being modified.        */
/* Justification : Typecasting from void* is necessary to hide internal types */
/*                 from the header files which are exposed to user.           */
/* Verification  : However, part of the code is verified manually and it is   */
/*                 not having any impact.                                     */
/* Reference     : Look for START Msg(4:2985)-5 and                           */
/*                 END Msg(4:2985)-5 tags in the code.                        */
/******************************************************************************/

/******************************************************************************/
/**                      QAC warning                                         **/
/******************************************************************************/
/* 1. QAC warning:                                                            */
/* Message       : (2:3227) The parameter '%s' is never modified and          */
/*                  so it could be declared with the 'const' qualifier.       */
/* Rule          : No MISRA-C:2004 Rules applicable to message 3227           */
/*                 REFERENCE - ISO:C90-6.5.3 Type Qualifiers                  */
/* Justification : The value of this function parameter is never modified.    */
/*                 It could be declared with the 'const' type qualifier.      */
/* Verification  : 'const' can be applied to any variable whose value needs   */
/*                 to be set only once providing it can be initialized        */
/*                 when it is defined.                                        */
/* Reference     : Look for START Msg(2:3227)-1 and                           */
/*                 END Msg(2:3227)-1 tags in the code.                        */
/******************************************************************************/
/* 2. QAC warning:                                                            */
/* Message       : (2:3416) Logical operation performed on expression with    */
/*                 possible side effects.                                     */
/* Rule          : No MISRA-C:2004 Rules applicable to message 3416           */
/* Justification : Logical operation performed on expression with             */
/*                 possible side effects.                                     */
/* Verification  : However, part of the code is verified manually and it is   */
/*                 not having any impact.                                     */
/* Reference     : Look for START Msg(2:3416)-2 and                           */
/*                 END Msg(2:3416)-2 tags in the code.                        */
/******************************************************************************/
/* 3. QAC warning:                                                            */
/* Message       : (2:3441) Function call argument is an expression with      */
/*                 possible side effects                                      */
/* Rule          : No MISRA-C:2004 Rules applicable to message 3441           */
/* Justification : Since it is a const variable, there is no possibility of   */
/*                 side effects.                                              */
/* Verification  : However, part of the code is verified manually and it is   */
/*                 not having any impact.                                     */
/* Reference     : Look for START Msg(2:3441)-3 and                           */
/*                 END Msg(2:3441)-3 tags in the code.                        */
/******************************************************************************/
/* 4. QAC Warning:                                                            */
/* Message       : (2:2016) This 'switch' statement 'default' clause is empty.*/
/* Rule          : No MISRA-C:2004 Rules applicable to message 2016           */
/* Justification : Intended design                                            */
/* Verification  : However, part of the code is verified manually and it is   */
/*                 not having any impact.                                     */
/* Reference     : Look for START Msg(2:2016)-4 and                           */
/*                 END Msg(2:2016)-4 tags in the code.                        */
/******************************************************************************/
/* 5. QAC Warning:                                                            */
/* Message       : (2:3206) The parameter '%s' is not used                    */
/*                 in this function.                                          */
/* Rule          : No MISRA-C:2004 Rules applicable to message 2016           */
/* Justification : Intended design                                            */
/* Verification  : However, part of the code is verified manually and it is   */
/*                 not having any impact.                                     */
/* Reference     : Look for START Msg(2:3206)-5 and                           */
/*                 END Msg(2:3206)-5 tags in the code.                        */
/******************************************************************************/

/*******************************************************************************
**                         Global Data                                        **
*******************************************************************************/

/*******************************************************************************
**                      Function Definitions                                  **
*******************************************************************************/

/*******************************************************************************
** Function Name         : Lin_TxIsr
**
** Service ID            : None
**
** Description           : Interrupt Service Handler for transmission interrupt.
**
** Sync/Async            : Sync
**
** Reentrancy            : Non-Reentrant for same channel
**
** Input Parameters      : LucChannel : Number of LIN channel
**
** InOut Parameters      : None
**
** Output Parameters     : None
**
** Return parameter      : None
**
** Preconditions         : None
**
** Global Variable(s)    : Lin_GpChannelRamData, Lin_GpRLIN3Properties
**
** Function(s) invoked   : Dem_ReportErrorStatus,
**                         Lin_HwWakeupOnGoing(when calling Lin_Wakeup),
**                         Lin_HwProcessData(when callin Lin_SendFrame with
**                         LIN_MASTER_RESPONSE or Lin_GoToSleep)
**                         LIN_ENTER_CRITICAL_SECTION, LIN_EXIT_CRITICAL_SECTION
**
** Registers Used        : None
**
** Reference ID          : LIN_DDD_ACT_013, LIN_DDD_ACT_013_CRT001, 
** Reference ID          : LIN_DDD_ACT_013_CRT002, LIN_DDD_ACT_013_ERR001, 
** Reference ID          : LIN_DDD_ACT_013_FSR001
*******************************************************************************/
#define LIN_START_SEC_CODE_FAST
#include "Lin_MemMap.h"
FUNC(void, LIN_CODE_FAST) Lin_TxIsr(const uint8 LucChannel)
{
#if (LIN_INTERRUPT_CONSISTENCY_CHECK == STD_ON)
  /* Check if the EIMK bit of the EIC register is set */
  /* MISRA Violation: START Msg(4:0491)-2 */
  if (LIN_EIC_EIMK_MASK ==
    (uint16)((*(Lin_GpRLIN3Properties[LucChannel].pLin3IntTxEicReg))
      & LIN_EIC_EIMK_MASK))
  /* END Msg(4:0491)-2 */
  {
    #ifdef LIN_E_INT_INCONSISTENT
    /* Reporting to DEM that interrupt from unknown source */
    Dem_ReportErrorStatus(LIN_E_INT_INCONSISTENT, DEM_EVENT_STATUS_FAILED);
    #endif
  }
  else
#endif /* (LIN_INTERRUPT_CONSISTENCY_CHECK == STD_ON) */
  {
    #if (LIN_CRITICAL_SECTION_PROTECTION == STD_ON)
    /* Enter the protection area */
    LIN_ENTER_CRITICAL_SECTION(LIN_RAM_DATA_PROTECTION);
    #endif
    
    /* MISRA Violation: START Msg(4:0491)-2 */
    if (LIN_TRUE == Lin_GpChannelRamData[LucChannel].blWakeupCalled)
    /* END Msg(4:0491)-2 */
    {
      Lin_HwWakeupOnGoing(LucChannel);
    }
    else
    {
      /* Invoke Lin_HwProcessData to deal with the Tx Interrupt */
      Lin_HwProcessData(LucChannel);
    }
    
    #if (LIN_CRITICAL_SECTION_PROTECTION == STD_ON)
    /* Exit the protection area */
    LIN_EXIT_CRITICAL_SECTION(LIN_RAM_DATA_PROTECTION);
    #endif
  }
}
#define LIN_STOP_SEC_CODE_FAST
#include "Lin_MemMap.h"

/*******************************************************************************
** Function Name         : Lin_RxIsr
**
** Service ID            : None
**
** Description           : Interrupt Service Handler for reception interrupt.
**
** Sync/Async            : Sync
**
** Reentrancy            : Non-Reentrant for same channel
**
** Input Parameters      : LucChannel : Number of LIN channel
**
** InOut Parameters      : None
**
** Output Parameters     : None
**
** Return parameter      : None
**
** Preconditions         : None
**
** Global Variable(s)    : Lin_GpChannelRamData, Lin_GpRLIN3Properties
**
** Function(s) invoked   : Dem_ReportErrorStatus,
**                         Lin_HwWakeUpFromBus(when detecting a wake-up pulse),
**                         Lin_HwProcessData(when calling Lin_SendFrame with
**                         LIN_SLAVE_RESPONSE)
**                         LIN_ENTER_CRITICAL_SECTION, LIN_EXIT_CRITICAL_SECTION
**
** Registers Used        : None
**
** Reference ID          : LIN_DDD_ACT_014, LIN_DDD_ACT_014_CRT001, 
** Reference ID          : LIN_DDD_ACT_014_CRT002, LIN_DDD_ACT_014_ERR001, 
** Reference ID          : LIN_DDD_ACT_014_FSR001
*******************************************************************************/
#define LIN_START_SEC_CODE_FAST
#include "Lin_MemMap.h"
FUNC(void, LIN_CODE_FAST) Lin_RxIsr(const uint8 LucChannel)
{
#if (LIN_INTERRUPT_CONSISTENCY_CHECK == STD_ON)
  /* Check if the EIMK bit of the EIC register is set */
  /* MISRA Violation: START Msg(4:0491)-2 */
  if (LIN_EIC_EIMK_MASK ==
    (uint16)((*(Lin_GpRLIN3Properties[LucChannel].pLin3IntRxEicReg))
      & LIN_EIC_EIMK_MASK))
  /* END Msg(4:0491)-2 */
  {
    #ifdef LIN_E_INT_INCONSISTENT
    /* Reporting to DEM that interrupt from unknown source */
    Dem_ReportErrorStatus(LIN_E_INT_INCONSISTENT, DEM_EVENT_STATUS_FAILED);
    #endif
  }
  else
#endif /* (LIN_INTERRUPT_CONSISTENCY_CHECK == STD_ON) */
  {
  /* Check if global wakeup support is enabled */
  #if (LIN_WAKEUP_SUPPORT == STD_ON)
    /* Check if channel status is sleep */
    /* MISRA Violation: START Msg(4:0491)-2 */
    if ( (LIN_CH_SLEEP ==
           Lin_GpChannelRamData[LucChannel].enChannelStatus) || 
         (LIN_TRUE ==
           Lin_GpChannelRamData[LucChannel].blSleepPending) )
    /* END Msg(4:0491)-2 */
    {
      /* Invoke Lin_HwWakeUpFromBus() */
      Lin_HwWakeUpFromBus(LucChannel);
    }
    else
  #endif
    {
    #if (LIN_CRITICAL_SECTION_PROTECTION == STD_ON)
    /* Enter the protection area */
    LIN_ENTER_CRITICAL_SECTION(LIN_RAM_DATA_PROTECTION);
    #endif

    /* Invoke Lin_HwProcessData to deal with the Rx Interrupt */
    Lin_HwProcessData(LucChannel);

    #if (LIN_CRITICAL_SECTION_PROTECTION == STD_ON)
    /* Exit the protection area */
    LIN_EXIT_CRITICAL_SECTION(LIN_RAM_DATA_PROTECTION);
    #endif
    }
  }
}
#define LIN_STOP_SEC_CODE_FAST
#include "Lin_MemMap.h"

/*******************************************************************************
** Function Name         : Lin_ErrIsr
**
** Service ID            : None
**
** Description           : Interrupt Service Handler for error interrupt.
**
** Sync/Async            : Sync
**
** Reentrancy            : Non-Reentrant for same channel
**
** Input Parameters      : LucChannel : Number of LIN channel
**
** InOut Parameters      : None
**
** Output Parameters     : None
**
** Return parameter      : None
**
** Preconditions         : None
**
** Global Variable(s)    : Lin_GpRLIN3Properties
**
** Function(s) invoked   : Dem_ReportErrorStatus,
**                         Lin_HwErrorProcessing,
**                         LIN_ENTER_CRITICAL_SECTION, LIN_EXIT_CRITICAL_SECTION
**
** Registers Used        : None
**
** Reference ID          : LIN_DDD_ACT_015, LIN_DDD_ACT_015_CRT001, 
** Reference ID          : LIN_DDD_ACT_015_CRT002, LIN_DDD_ACT_015_ERR001, 
** Reference ID          : LIN_DDD_ACT_015_FSR001
*******************************************************************************/
#define LIN_START_SEC_CODE_FAST
#include "Lin_MemMap.h"
FUNC(void, LIN_CODE_FAST) Lin_ErrIsr(const uint8 LucChannel)
{
#if (LIN_INTERRUPT_CONSISTENCY_CHECK == STD_ON)
  /* Check if the EIMK bit of the EIC register is set */
  /* MISRA Violation: START Msg(4:0491)-2 */
  if (LIN_EIC_EIMK_MASK ==
     (uint16)((*(Lin_GpRLIN3Properties[LucChannel].pLin3IntStEicReg))
     & LIN_EIC_EIMK_MASK))
  /* END Msg(4:0491)-2 */
  {
    #ifdef LIN_E_INT_INCONSISTENT
    /* Reporting to DEM that interrupt from unknown source */
    Dem_ReportErrorStatus(LIN_E_INT_INCONSISTENT, DEM_EVENT_STATUS_FAILED);
    #endif
  }
  else
#endif /* (LIN_INTERRUPT_CONSISTENCY_CHECK == STD_ON) */
  {
    #if (LIN_CRITICAL_SECTION_PROTECTION == STD_ON)
    /* Enter the protection area */
    LIN_ENTER_CRITICAL_SECTION(LIN_RAM_DATA_PROTECTION);
    #endif
    
    /* Calling error processing API for error check */
    Lin_HwErrorProcessing(LucChannel);
    
    #if (LIN_CRITICAL_SECTION_PROTECTION == STD_ON)
    /* Exit the protection area */
    LIN_EXIT_CRITICAL_SECTION(LIN_RAM_DATA_PROTECTION);
    #endif
  }
}
#define LIN_STOP_SEC_CODE_FAST
#include "Lin_MemMap.h"

/*******************************************************************************
** Function Name      : Lin_HwSendHeaderInternal
**
** Service ID         : NA
**
** Description        : Internal Function to start frame transmission
**
** Sync/Async         : Synchronous
**
** Re-entrancy        : Non Re-entrant
**
** Input Parameters   : LucChannel, LpPduInfoPtr, LucApiId
**
** Output Parameters  : None
**
** Return parameter   : None
**
** Preconditions      : None
**
** Global Variables   : Lin_GpChannelConfig, Lin_GpChannelRamData,
**                      Lin_GpRLIN3Properties
**
** Functions invoked  : Lin_HwLoadTxBuffer, Lin_HwSetMasterMode,
**                      Lin_HwScheduleInterrupt
**
** Registers Used     : ucRLN3nLIDB,
**                      ucRLN3nLDFC,
**                      ucRLN3nLTRC
**
** Reference ID       : LIN_DDD_ACT_016, LIN_DDD_ACT_016_GBL001, 
** Reference ID       : LIN_DDD_ACT_016_GBL002, LIN_DDD_ACT_016_GBL003, 
** Reference ID       : LIN_DDD_ACT_016_GBL004, LIN_DDD_ACT_016_GBL005, 
** Reference ID       : LIN_DDD_ACT_016_GBL006, LIN_DDD_ACT_016_GBL007, 
** Reference ID       : LIN_DDD_ACT_016_GBL008, LIN_DDD_ACT_016_REG001, 
** Reference ID       : LIN_DDD_ACT_016_REG002, LIN_DDD_ACT_016_REG003, 
** Reference ID       : LIN_DDD_ACT_016_REG004, LIN_DDD_ACT_016_REG005, 
** Reference ID       : LIN_DDD_ACT_016_REG006, LIN_DDD_ACT_016_REG007, 
** Reference ID       : LIN_DDD_ACT_016_REG008
*******************************************************************************/
#define LIN_START_SEC_PRIVATE_CODE
#include "Lin_MemMap.h"
FUNC(Std_ReturnType, LIN_PRIVATE_CODE) Lin_HwSendHeaderInternal(
           const uint8 LucChannel,
           CONSTP2CONST(Lin_PduType, AUTOMATIC, LIN_PRIVATE_CONST)LpPduInfoPtr,
           const uint8 LucApiId)
{
  volatile P2CONST(Lin_ChannelInfo, AUTOMATIC, LIN_PRIVATE_CONST)
                                                             LpLinChannelPtr;
  volatile P2CONST(Lin3_ChannelConfigType, AUTOMATIC, LIN_PRIVATE_CONST)
                                                             LpLin3ChannelPtr;
  volatile P2VAR(RLin3_UartRegs, AUTOMATIC, REGSPACE)LpLN3ChannelRegs;
  volatile P2VAR(Lin_RamData, AUTOMATIC, LIN_VAR)LpRamVars;
  Std_ReturnType LucReturnValue;

  Lin_FrameResponseType LenFrameType;

  /* Initialize the return value */
  /* MISRA Violation: START Msg(4:2982)-4 */
  LucReturnValue = E_NOT_OK;
  /* END Msg(4:2982)-4 */

  /* Get the pointer to channel RAM data */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpRamVars = &Lin_GpChannelRamData[LucChannel];
  /* END Msg(4:0491)-2 */

  /* Copy the frame Id to frame data structure */
  LpRamVars->ucFrameId = LpPduInfoPtr->Pid;

  /* Copy the checksum model to frame data structure */
  LpRamVars->enCheckSumModel = LpPduInfoPtr->Cs;

  /* Copy the data length to frame data structure */
  LpRamVars->ucFrameLength = LpPduInfoPtr->Dl;

  /* Copy the frame type to frame data structure */
  LpRamVars->enFrameType = LpPduInfoPtr->Drc;

  /* Get the address of LIN Channel structure */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpLinChannelPtr = &Lin_GpChannelConfig[LucChannel];
  /* END Msg(4:0491)-2 */

  /* MISRA Violation: START Msg(4:0491)-2 */
  LpLin3ChannelPtr =
                 &Lin_GpRLIN3Properties[LpLinChannelPtr->ucPropertiesIndex];
  /* END Msg(4:0491)-2 */

  /* Get the RLIN3 Channel Base Address */
  LpLN3ChannelRegs = LpLin3ChannelPtr->pLn3ChanlBaseAddress;

  /* Get frame type */
  LenFrameType = LpRamVars->enFrameType;

  if (LIN_TRUE == LpRamVars->blWakeupCalled)
  {
    /* Reset the LinIf wake-up flag */
    LpRamVars->blWakeupCalled = LIN_FALSE;
  }
  else
  {
    /* No action required */
  }

  /* Set RLIN3 to Reset Mode */
  LucReturnValue = Lin_HwSetMasterMode(LucChannel, RLIN3_RESET_MODE, LucApiId);
  
  if (E_OK == LucReturnValue)
  {
    /* Set RLIN3 to Operation Mode */
    LucReturnValue = Lin_HwSetMasterMode(LucChannel,
                                            RLIN3_OPERATION_MODE, LucApiId);
  }
  else
  {
    /* No action required */
  }

  if (E_OK == LucReturnValue)
  {
    /* Set the frame Id */
    LpLN3ChannelRegs->ucRLN3nLIDB = LpRamVars->ucFrameId;
    
    /* Reset of LIN data Field Configuration Register */
    LpLN3ChannelRegs->ucRLN3nLDFC = LIN_RESET_VALUE;
    
    /* If checksum model is of type CLASSIC */
    if (LIN_CLASSIC_CS == LpRamVars->enCheckSumModel)
    {
      /* Set the checksum model to CLASSIC */
      LpLN3ChannelRegs->ucRLN3nLDFC = (LpLN3ChannelRegs->ucRLN3nLDFC &
                                                           LIN_SET_CLS_CHKSUM);
    }
    /* If checksum model is of type ENHANCED */
    else
    {
      /* Set the checksum model to ENHANCED */
      LpLN3ChannelRegs->ucRLN3nLDFC = (LpLN3ChannelRegs->ucRLN3nLDFC |
                                                           LIN_SET_ENH_CHKSUM);
    }
    
    /* Set length of total bytes in a RLIN3 Frame(Response data + Checksum) */
    LpLN3ChannelRegs->ucRLN3nLDFC = (LpLN3ChannelRegs->ucRLN3nLDFC |
                                                             LpPduInfoPtr->Dl);
    
    if (LIN_MASTER_RESPONSE == LenFrameType)
    {
      /* To Set the Transmit Data */
      /*Call the internal function to load Tx Buffer and start the frame
        transmission */
      Lin_HwLoadTxBuffer(LucChannel);
    
      /* Set Response direction for Transmit */
      LpLN3ChannelRegs->ucRLN3nLDFC = (LpLN3ChannelRegs->ucRLN3nLDFC |
                                                         LIN_MASTER_DIRECTION);
    }
    else
    {
      /* Set Response direction for Receive */
      LpLN3ChannelRegs->ucRLN3nLDFC = (LpLN3ChannelRegs->ucRLN3nLDFC &
                                                         LIN_SLAVE_DIRECTION);
    }
    
    /* Enable RLIN3 Interrupt */
    Lin_HwScheduleInterrupt(LucChannel, LIN_INTERRUPT_EN);
    
    switch (LenFrameType)
    {
    case LIN_MASTER_RESPONSE:
      /* Check if the transmitted frame is for go-to-sleep command */
      if (LIN_SET_SLEEP_REQUEST == LpRamVars->ucSlpRqst_RespRdy)
      {
        /* No action required, keep LpRamVars->blSleepPending as LIN_TRUE */
      }
      else
      {
        /* Set the Channel Status */
        LpRamVars->enChannelStatus = LIN_TX_BUSY;
      }
      break;
    case LIN_SLAVE_TO_SLAVE:
      /* Set the Channel Status */
      LpRamVars->enChannelStatus = LIN_TX_BUSY;
      break;
    case LIN_SLAVE_RESPONSE:
      /* Set the Channel Status */
      LpRamVars->enChannelStatus = LIN_RX_BUSY;
      break;
      /* QAC Warning: START Msg(2:2016)-4 */
    default:
      /* No action required */
      break;
      /* END Msg(2:2016)-4 */
    }
    
    /* Initiate Transmission */
    LpLN3ChannelRegs->ucRLN3nLTRC = (LpLN3ChannelRegs->ucRLN3nLTRC |
                                                         LIN_START_TRANSMIT);
  } /* end of if (E_OK == LucReturnValue) */
  else
  {
    /* No action required */
  }
  return (LucReturnValue);
}
#define LIN_STOP_SEC_PRIVATE_CODE
#include "Lin_MemMap.h"

/*******************************************************************************
** Function Name      : Lin_HwWakeUpFromBus
**
** Service ID         : NA
**
** Description        : This service does wake-up (from the bus) processing
**
** Sync/Async         : Synchronous
**
** Re-entrancy        : Non Re-entrant for the same channel
**
** Input Parameters   : LucChannel
**
** Output Parameters  : None
**
** Return parameter   : None
**
** Preconditions      : None
**
** Global Variables   : Lin_GpChannelConfig, Lin_GpChannelRamData,
**                      Lin_GpRLIN3Properties
**
** Functions invoked  : EcuM_CheckWakeup
**
** Registers Used     : ucRLN3nLST
**
** Reference ID       : LIN_DDD_ACT_017, LIN_DDD_ACT_017_GBL001, 
** Reference ID       : LIN_DDD_ACT_017_REG001
*******************************************************************************/
#if (LIN_WAKEUP_SUPPORT == STD_ON)
#define LIN_START_SEC_CODE_FAST
#include "Lin_MemMap.h"
STATIC FUNC(void, LIN_CODE_FAST) Lin_HwWakeUpFromBus(const uint8 LucChannel)
{
  volatile P2CONST(Lin_ChannelInfo, AUTOMATIC, LIN_CONST)LpLinChannelPtr;
  volatile P2CONST(Lin3_ChannelConfigType, AUTOMATIC, LIN_CONST)
                                                          LpLin3ChannelPtr;
  volatile P2VAR(RLin3_UartRegs, AUTOMATIC, REGSPACE)LpLN3ChannelRegs;
  volatile P2VAR(Lin_RamData, AUTOMATIC, LIN_VAR)LpRamVars;

  /* Get the address of LIN Channel structure */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpLinChannelPtr = &Lin_GpChannelConfig[LucChannel];
  /* END Msg(4:0491)-2 */

  /* Get the pointer to channel RAM data */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpRamVars = &Lin_GpChannelRamData[LucChannel];
  /* END Msg(4:0491)-2 */

  /* Check if channel specific wake-up is enabled */
  if (LIN_TRUE == LpLinChannelPtr->blWakeupSupport)
  {
    /* MISRA Violation: START Msg(4:0491)-2 */
    LpLin3ChannelPtr =
                 &Lin_GpRLIN3Properties[LpLinChannelPtr->ucPropertiesIndex];
    /* END Msg(4:0491)-2 */

    /* Get the RLIN3 Channel Base Address */
    LpLN3ChannelRegs = LpLin3ChannelPtr->pLn3ChanlBaseAddress;

    if (LIN_RESPONSE_COMPLETE ==
               (uint8)(LpLN3ChannelRegs->ucRLN3nLST & LIN_RESPONSE_COMPLETE))
    {
      /* Clear sleep requested bit */
      LpRamVars->ucSlpRqst_RespRdy = LIN_WAKEUP;

      /* Clear the reception interrupt */
      LpLN3ChannelRegs->ucRLN3nLST = (LpLN3ChannelRegs->ucRLN3nLST &
                                                         LIN_RECEPTION_DONE);
    }
    else
    {
      /* No action required */
    }

    /* Invoke the upper layer wakeup notification */
    /* QAC Warning: START Msg(2:3441)-3 */
    EcuM_CheckWakeup(
        (EcuM_WakeupSourceType)LIN_ONE << (LpLinChannelPtr->ucWakeupSourceId));
    /* END Msg(2:3441)-3 */

  }/* Channel specific wakeup is enabled */
  else
  {
    /* No action required */
  }
}
#define LIN_STOP_SEC_CODE_FAST
#include "Lin_MemMap.h"
#endif /* #if (LIN_WAKEUP_SUPPORT == STD_ON) */

/*******************************************************************************
** Function Name      : Lin_HwProcessData
**
** Service ID         : NA
**
** Description        : This service processes the transmit and receive
**                      interrupt requests
**
** Sync/Async         : Synchronous
**
** Re-entrancy        : Non Re-entrant for the same channel
**
** Input Parameters   : LucChannel
**
** Output Parameters  : None
**
** Return parameter   : None
**
** Preconditions      : None
**
** Global Variables   : Lin_GpChannelConfig, Lin_GpChannelRamData,
**                      Lin_GpRLIN3Properties
**
** Functions invoked  : Lin_HwHandleWakeupRequest, Lin_HwScheduleInterrupt
**
** Registers Used     : ucRLN3nLST,  ucRLN3nLDBR
**
** Reference ID       : LIN_DDD_ACT_018, LIN_DDD_ACT_018_GBL001, 
** Reference ID       : LIN_DDD_ACT_018_GBL002, LIN_DDD_ACT_018_GBL003, 
** Reference ID       : LIN_DDD_ACT_018_GBL004, LIN_DDD_ACT_018_GBL005, 
** Reference ID       : LIN_DDD_ACT_018_GBL006, LIN_DDD_ACT_018_REG001, 
** Reference ID       : LIN_DDD_ACT_018_REG002, LIN_DDD_ACT_018_REG003, 
** Reference ID       : LIN_DDD_ACT_018_REG004, LIN_DDD_ACT_018_REG005, 
** Reference ID       : LIN_DDD_ACT_018_REG006
*******************************************************************************/
#define LIN_START_SEC_CODE_FAST
#include "Lin_MemMap.h"
STATIC FUNC(void, LIN_CODE_FAST) Lin_HwProcessData(const uint8 LucChannel)
{
  volatile P2CONST(Lin_ChannelInfo, AUTOMATIC, LIN_CONST)LpLinChannelPtr;
  volatile P2VAR(Lin_RamData, AUTOMATIC, LIN_VAR_FAST_NO_INIT)LpRamVars;
  volatile P2VAR(uint8, AUTOMATIC, LIN_VAR_FAST_NO_INIT)
                                                     volatile LpStartDataBuffer;
  Lin_FrameResponseType LenFrameType;
  uint8 LucIndex;
  uint8 LucDataLength;
  
  volatile P2CONST(Lin3_ChannelConfigType, AUTOMATIC, LIN_CONST) 
                                                           LpLin3ChannelPtr;
  volatile P2VAR(RLin3_UartRegs, AUTOMATIC, REGSPACE)LpLN3ChannelRegs;

  /* Get the pointer to requested channel configuration */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpLinChannelPtr = &Lin_GpChannelConfig[LucChannel];
  /* END Msg(4:0491)-2 */

  /* Get the pointer to channel RAM data */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpRamVars = &Lin_GpChannelRamData[LucChannel];
  /* END Msg(4:0491)-2 */

  /* MISRA Violation: START Msg(4:0491)-2 */
  LpLin3ChannelPtr =
                 &Lin_GpRLIN3Properties[LpLinChannelPtr->ucPropertiesIndex];
  /* END Msg(4:0491)-2 */

  /* Get the RLIN3 Channel Base Address */
  LpLN3ChannelRegs = LpLin3ChannelPtr->pLn3ChanlBaseAddress;
  
  /* Copy the data length to local variable */
  LucDataLength = LpRamVars->ucFrameLength;

  /* Check if the transmitted frame is for go-to-sleep command */
  if (LIN_SET_SLEEP_REQUEST == LpRamVars->ucSlpRqst_RespRdy)
  {
    /* Clear the sleep requested bit */
    LpRamVars->ucSlpRqst_RespRdy = LIN_CLR_SLEEP_REQUEST;

    /* Check for Frame Transmit Successfully */
    if (LIN_FRAME_COMPLETE ==
                  (uint8)(LpLN3ChannelRegs->ucRLN3nLST & LIN_FRAME_COMPLETE))
    {
      /* Clear the transmit interrupt */
      LpLN3ChannelRegs->ucRLN3nLST = (LpLN3ChannelRegs->ucRLN3nLST &
                                                          LIN_TRANSMIT_DONE);
    }
    else
    {
      /* No action required */
    }
    #if (LIN_WAKEUP_SUPPORT == STD_ON)
    /* Check if channel specific wake-up support is enabled */
    if (LIN_TRUE == LpLinChannelPtr->blWakeupSupport)
    {
      /* Set RLIN3 to Slave Wake-up */
      (void)Lin_HwHandleWakeupRequest(LucChannel, LIN_SLAVE_ISSUE, 
                                                LIN_GO_TO_SLEEP_SID);
    }
    else
    #endif /* #if (LIN_WAKEUP_SUPPORT == STD_ON) */
    {
      /* Disable RLIN3 Interrupt */
      Lin_HwScheduleInterrupt(LucChannel, LIN_INTERRUPT_DIS);
    }
  }
  /* If the transmitted frame is not a sleep frame */
  else
  {
    /* Get the frame type */
    LenFrameType = LpRamVars->enFrameType;

    switch (LenFrameType)
    {
    /* Frame is of type LIN_SLAVE_RESPONSE */
    case LIN_SLAVE_RESPONSE:
      /* If LATIT is generated for Header part in Rx Mode */
      if (LIN_RX_BUSY == LpRamVars->enChannelStatus)
      {
        if (LIN_HEADER_COMPLETE ==
               (uint8)(LpLN3ChannelRegs->ucRLN3nLST & LIN_HEADER_COMPLETE))
        {
          /* Clear the header interrupt */
          LpLN3ChannelRegs->ucRLN3nLST = (LpLN3ChannelRegs->ucRLN3nLST &
                                                          LIN_HEADER_DONE);
          /* Update channel status to LIN_RX_NO_RESPONSE */
          LpRamVars->enChannelStatus = LIN_RX_NO_RESPONSE;
        }
        else
        {
          /* No action required */
        }
      }
      else
      {
        /* No action required */
      }
      /* If LATIR is generated for response part in Rx Mode */
      if (LIN_RX_NO_RESPONSE == LpRamVars->enChannelStatus)
      {
        if (LIN_RESPONSE_COMPLETE ==
             (uint8)(LpLN3ChannelRegs->ucRLN3nLST & LIN_RESPONSE_COMPLETE))
        {
          /* Clear the reception interrupt */
          LpLN3ChannelRegs->ucRLN3nLST = (LpLN3ChannelRegs->ucRLN3nLST &
                                                       LIN_RECEPTION_DONE);

          LpStartDataBuffer = &LpRamVars->aaFrameData[(uint32)LIN_ZERO];

          /* Copy data to Tx Buffer */
          for (LucIndex = LIN_ZERO; LucDataLength > LucIndex; LucIndex++)
          {
            *LpStartDataBuffer = LpLN3ChannelRegs->ucRLN3nLDBR[LucIndex];

            /* MISRA Violation: START Msg(4:0489)-1 */
            LpStartDataBuffer++;
            /* END Msg(4:0489)-1 */
          }
          /* Update channel status to LIN_RX_OK */
          LpRamVars->enChannelStatus = LIN_RX_OK;
        }
        else
        {
          /* No action required */
        }
      }
      else
      {
        /* No action required */
      }
      break;
    /* Frame is of type LIN_MASTER_RESPONSE */
    case LIN_MASTER_RESPONSE:
      /* Check for Frame Transmit Successfully */
      if (LIN_FRAME_COMPLETE ==
               (uint8)(LpLN3ChannelRegs->ucRLN3nLST & LIN_FRAME_COMPLETE))
      {
        /* Clear the transmit interrupt */
        LpLN3ChannelRegs->ucRLN3nLST = (LpLN3ChannelRegs->ucRLN3nLST &
                                                        LIN_TRANSMIT_DONE);
        /* Update channel status to LIN_TX_OK */
        LpRamVars->enChannelStatus = LIN_TX_OK;
      }
      else
      {
        /* No action required */
      }
      break;
    /* Frame is of type LIN_SLAVE_TO_SLAVE */
    case LIN_SLAVE_TO_SLAVE:
      if (LIN_HEADER_COMPLETE ==
             (uint8)(LpLN3ChannelRegs->ucRLN3nLST & LIN_HEADER_COMPLETE))
      {
        /* Clear the header interrupt */
        LpLN3ChannelRegs->ucRLN3nLST = (LpLN3ChannelRegs->ucRLN3nLST &
                                                        LIN_HEADER_DONE);
        /* Set the channel status to LIN_TX_OK */
        LpRamVars->enChannelStatus = LIN_TX_OK;
      }
      else
      {
        /* No action required */
      }
      if (LIN_RESPONSE_COMPLETE ==
            (uint8)(LpLN3ChannelRegs->ucRLN3nLST & LIN_RESPONSE_COMPLETE))
      {
        /* Clear the reception interrupt */
        LpLN3ChannelRegs->ucRLN3nLST = (LpLN3ChannelRegs->ucRLN3nLST &
                                                    LIN_RECEPTION_DONE);
      }
      else
      {
        /* No action required */
      }
      break;
    /* QAC Warning: START Msg(2:2016)-4 */
    default:
      /* No action required */
      break;
    /* END Msg(2:2016)-4 */
    }
    /* Disable RLIN3 Interrupt */
    Lin_HwScheduleInterrupt(LucChannel, LIN_INTERRUPT_DIS);
  }
}
#define LIN_STOP_SEC_CODE_FAST
#include "Lin_MemMap.h"

/*******************************************************************************
** Function Name      : Lin_HwErrorProcessing
**
** Service ID         : NA
**
** Description        : This service processes the status interrupt requests
**
** Sync/Async         : Synchronous
**
** Re-entrancy        : Non Re-entrant for the same channel
**
** Input Parameters   : uint8 LucChannel
**
** Output Parameters  : None
**
** Return parameter   : None
**
** Preconditions      : None
**
** Global Variables   : Lin_GpChannelConfig, Lin_GpChannelRamData,
**                      Lin_GpRLIN3Properties
**
** Functions invoked  : Dem_ReportErrorStatus, Lin_HwHandleWakeupRequest,
**                      Lin_HwScheduleInterrupt
**
** Registers Used     : ucRLN3nLEST, ucRLN3nLST
**
** Reference ID       : LIN_DDD_ACT_019, LIN_DDD_ACT_019_ERR001, 
** Reference ID       : LIN_DDD_ACT_019_ERR002, LIN_DDD_ACT_019_GBL001, 
** Reference ID       : LIN_DDD_ACT_019_GBL002, LIN_DDD_ACT_019_GBL003, 
** Reference ID       : LIN_DDD_ACT_019_GBL004, LIN_DDD_ACT_019_GBL005, 
** Reference ID       : LIN_DDD_ACT_019_GBL006, LIN_DDD_ACT_019_GBL007, 
** Reference ID       : LIN_DDD_ACT_019_GBL008, LIN_DDD_ACT_019_GBL009, 
** Reference ID       : LIN_DDD_ACT_019_GBL010, LIN_DDD_ACT_019_GBL011, 
** Reference ID       : LIN_DDD_ACT_019_GBL012, LIN_DDD_ACT_019_GBL013, 
** Reference ID       : LIN_DDD_ACT_019_GBL014, LIN_DDD_ACT_019_GBL015, 
** Reference ID       : LIN_DDD_ACT_019_GBL016, LIN_DDD_ACT_019_GBL017, 
** Reference ID       : LIN_DDD_ACT_019_REG001, LIN_DDD_ACT_019_REG002, 
** Reference ID       : LIN_DDD_ACT_019_REG003, LIN_DDD_ACT_019_REG004, 
** Reference ID       : LIN_DDD_ACT_019_REG005, LIN_DDD_ACT_019_REG006, 
** Reference ID       : LIN_DDD_ACT_019_REG007, LIN_DDD_ACT_019_REG008, 
** Reference ID       : LIN_DDD_ACT_019_REG009, LIN_DDD_ACT_019_REG010, 
** Reference ID       : LIN_DDD_ACT_019_REG011, LIN_DDD_ACT_019_REG012, 
** Reference ID       : LIN_DDD_ACT_019_REG013, LIN_DDD_ACT_019_REG014, 
** Reference ID       : LIN_DDD_ACT_019_REG015
*******************************************************************************/
#define LIN_START_SEC_CODE_FAST
#include "Lin_MemMap.h"
STATIC FUNC(void, LIN_CODE_FAST) Lin_HwErrorProcessing(const uint8 LucChannel)
{
  volatile P2CONST(Lin_ChannelInfo, AUTOMATIC, LIN_CONST)LpLinChannelPtr;
  volatile P2CONST(Lin3_ChannelConfigType, AUTOMATIC, LIN_CONST)
                                                              LpLin3ChannelPtr;
  volatile P2VAR(RLin3_UartRegs, AUTOMATIC, REGSPACE)LpLN3ChannelRegs;
  volatile P2VAR(Lin_RamData, AUTOMATIC, LIN_VAR_FAST_NO_INIT)LpRamVars;

  Lin_FrameResponseType LenFrameType;

  /* Get the pointer to requested channel configuration */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpLinChannelPtr = &Lin_GpChannelConfig[LucChannel];
  /* END Msg(4:0491)-2 */

  /* Get the pointer to channel RAM data */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpRamVars = &Lin_GpChannelRamData[LucChannel];
  /* END Msg(4:0491)-2 */

  /* Get the channel index */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpLin3ChannelPtr =
               &Lin_GpRLIN3Properties[LpLinChannelPtr->ucPropertiesIndex];
  /* END Msg(4:0491)-2 */

  /* Get the RLIN3 Channel Base Address */
  LpLN3ChannelRegs = LpLin3ChannelPtr->pLn3ChanlBaseAddress;

  /* Get frame type */
  LenFrameType = LpRamVars->enFrameType;

  /* Check for any error occurred */
  if (LIN_ERROR_OCCURED ==
              (uint8)(LpLN3ChannelRegs->ucRLN3nLST & LIN_ERROR_OCCURED))
  {
    /* Check if the transmitted frame is for go-to-sleep command */
    if (LIN_SET_SLEEP_REQUEST == LpRamVars->ucSlpRqst_RespRdy)
    {
      /* LIN_CH_SLEEP:                                                       */
      /* Lin_GetStatus() has already been called after calling Lin_GoToSleep */
      /* or                                                                  */
      /* LIN_CH_SLEEP_PENDING:                                               */
      /* Lin_GetStatus() has not been called yet after calling Lin_GoToSleep */
      /* The status of channel must not be changed while issuing the         */
      /* go-to-sleep command even if some errors have occured.               */
      if ( (LIN_CH_SLEEP == LpRamVars->enChannelStatus) ||
           (LIN_TRUE == LpRamVars->blSleepPending) )
      {
        /* Clear the sleep requested bit */
        LpRamVars->ucSlpRqst_RespRdy = LIN_CLR_SLEEP_REQUEST;

        if (LIN_HEADER_COMPLETE ==
          (uint8)(LpLN3ChannelRegs->ucRLN3nLST & LIN_HEADER_COMPLETE))
        {
          /* Clear the header interrupt */
          LpLN3ChannelRegs->ucRLN3nLST = (LpLN3ChannelRegs->ucRLN3nLST &
                                                             LIN_HEADER_DONE);
        }
        else
        {
          /* No action required */
        }

        /* Clear all error flags and the ERR bit of the RLN3nLST register
           is automatically cleared */
        LpLN3ChannelRegs->ucRLN3nLEST = LIN_RESET_VALUE;

        #if (LIN_WAKEUP_SUPPORT == STD_ON)
        /* Check if channel specific wake-up support is enabled */
        if (LIN_TRUE == LpLinChannelPtr->blWakeupSupport)
        {
          /* Set RLIN3 to Slave Wake-up */
          (void)Lin_HwHandleWakeupRequest(LucChannel, LIN_SLAVE_ISSUE,
                                                LIN_GO_TO_SLEEP_SID);
        }
        else
        #endif /* #if (LIN_WAKEUP_SUPPORT == STD_ON) */
        {
          /* Disable RLIN3 Interrupt */
          Lin_HwScheduleInterrupt(LucChannel, LIN_INTERRUPT_DIS);
        }
      }
      else
      {
        /* No action required */
      }
    }
    /* Check if an error was occurred in Lin_Wakeup() */
    else if (LIN_TRUE == LpRamVars->blWakeupCalled)
    {
      /* Reset the LinIf wake-up flag */
      LpRamVars->blWakeupCalled = LIN_FALSE;

      /* Check for Bit error */
      if (LIN_BIT_ERROR ==
                  (uint8)(LpLN3ChannelRegs->ucRLN3nLEST & LIN_BIT_ERROR))
      {
        /* Update channel status to LIN_TX_HEADER_ERROR */
        LpRamVars->enChannelStatus = LIN_TX_HEADER_ERROR;

        /* Clear BER bit */
        LpLN3ChannelRegs->ucRLN3nLEST =
                              (LpLN3ChannelRegs->ucRLN3nLEST & LIN_BIT_CLEAR);
      }
      else
      {
        /* No action required */
      }

      /* Check for Header Transmission error */
      if (LIN_PBUS_ERROR ==
                      (uint8)(LpLN3ChannelRegs->ucRLN3nLEST & LIN_PBUS_ERROR))
      {
        /* Update channel status to LIN_TX_HEADER_ERROR */
        LpRamVars->enChannelStatus = LIN_TX_HEADER_ERROR;

        /* Clear PBER bit */
        LpLN3ChannelRegs->ucRLN3nLEST =
                          (LpLN3ChannelRegs->ucRLN3nLEST & LIN_PBUS_CLEAR);
      }
      else
      {
        /* No action is required */
      }

      /* Disable RLIN3 Interrupt */
      Lin_HwScheduleInterrupt(LucChannel, LIN_INTERRUPT_DIS);
    }
    /* Other than go-to-sleep command and Lin_Wakeup(), 
       Lin_SendFrame() with LIN_SLAVE_TO_SLAVE */
    else if (LIN_SLAVE_TO_SLAVE == LenFrameType)
    {
      /* an error was occurred in response part */
      if (LIN_HEADER_COMPLETE ==
             (uint8)(LpLN3ChannelRegs->ucRLN3nLST & LIN_HEADER_COMPLETE))
      {
        /* Clear the header interrupt */
        LpLN3ChannelRegs->ucRLN3nLST = (LpLN3ChannelRegs->ucRLN3nLST &
                                                        LIN_HEADER_DONE);

        /* Set the channel status to LIN_TX_OK */
        LpRamVars->enChannelStatus = LIN_TX_OK;
        
        /* Clear all error flags and the ERR bit of the RLN3nLST register
         is automatically cleared */
        LpLN3ChannelRegs->ucRLN3nLEST = LIN_RESET_VALUE;
      }
      /* an error was occurred in header part */
      else
      {
        /* Check for Bit error */
        if (LIN_BIT_ERROR ==
                    (uint8)(LpLN3ChannelRegs->ucRLN3nLEST & LIN_BIT_ERROR))
        {
          /* Update channel status to LIN_TX_HEADER_ERROR */
          LpRamVars->enChannelStatus = LIN_TX_HEADER_ERROR;

          /* Clear BER bit */
          LpLN3ChannelRegs->ucRLN3nLEST =
                                (LpLN3ChannelRegs->ucRLN3nLEST & LIN_BIT_CLEAR);
        }
        else
        {
          /* No action required */
        }

        /* Check for Header Transmission error */
        if (LIN_PBUS_ERROR ==
                        (uint8)(LpLN3ChannelRegs->ucRLN3nLEST & LIN_PBUS_ERROR))
        {
          /* Update channel status to LIN_TX_HEADER_ERROR */
          LpRamVars->enChannelStatus = LIN_TX_HEADER_ERROR;

          /* Clear PBER bit */
          LpLN3ChannelRegs->ucRLN3nLEST =
                            (LpLN3ChannelRegs->ucRLN3nLEST & LIN_PBUS_CLEAR);
        }
        else
        {
          /* No action is required */
        }

        /* Check for any Time out error */
        if (LIN_TIMEOUT_ERROR ==
                  (uint8)(LpLN3ChannelRegs->ucRLN3nLEST & LIN_TIMEOUT_ERROR))
        {
          /* Update channel status to LIN_TX_HEADER_ERROR */
          LpRamVars->enChannelStatus = LIN_TX_HEADER_ERROR;

          #ifdef LIN_E_TIMEOUT_FAILURE
          /* Report Error to DEM */
          Dem_ReportErrorStatus(LIN_E_TIMEOUT_FAILURE, DEM_EVENT_STATUS_FAILED);
          #endif

          /* Clear FTER bit */
          LpLN3ChannelRegs->ucRLN3nLEST =
                           (LpLN3ChannelRegs->ucRLN3nLEST & LIN_TIMEOUT_CLEAR);
        }
        else
        {
          /* No action required */
        }
      }

      /* Disable RLIN3 Interrupt */
      Lin_HwScheduleInterrupt(LucChannel, LIN_INTERRUPT_DIS);
    }
    /* Lin_SendFrame() with LIN_MASTER_RESPONSE and LIN_SLAVE_RESPONSE */
    else
    {
      /* Check for any checksum field error */
      if (LIN_CHECKSUM_ERROR ==
               (uint8)(LpLN3ChannelRegs->ucRLN3nLEST & LIN_CHECKSUM_ERROR))
      {
        /* Update channel status to LIN_RX_ERROR */
        LpRamVars->enChannelStatus = LIN_RX_ERROR;

        /* Clear CKE bit */
        LpLN3ChannelRegs->ucRLN3nLEST =
                         (LpLN3ChannelRegs->ucRLN3nLEST & LIN_CHECKSUM_CLEAR);
      }
      else
      {
        /* No action required */
      }

      /* Check for any Time out error */
      if (LIN_TIMEOUT_ERROR ==
                (uint8)(LpLN3ChannelRegs->ucRLN3nLEST & LIN_TIMEOUT_ERROR))
      {
        /* Check if header frame transmit successfully */
        if (LIN_HEADER_COMPLETE ==
              (uint8)(LpLN3ChannelRegs->ucRLN3nLST & LIN_HEADER_COMPLETE))
        {
          if (LIN_MASTER_RESPONSE == LenFrameType)
          {
            /* an error was occurred in response transmission part */
            /* Update channel status to LIN_TX_ERROR */
            LpRamVars->enChannelStatus = LIN_TX_ERROR;
          }
          /* frame type is of LIN_SLAVE_RESPONSE */
          else
          {
            /* an error was occurred in response reception part */
            /* Check if at least 1 response byte has been received */
            if (LIN_DATA1RX_COMPLETE ==
              (uint8)(LpLN3ChannelRegs->ucRLN3nLST & LIN_DATA1RX_COMPLETE))
            {
              /* Update channel status to LIN_RX_ERROR */
              LpRamVars->enChannelStatus = LIN_RX_ERROR;
            }
            /* if no response byte has been received */
            else
            {
              /* Update channel status to LIN_RX_NO_RESPONSE */
              LpRamVars->enChannelStatus = LIN_RX_NO_RESPONSE;
            }
          }
        }
        /* an error was occurred in header part */
        else
        {
          /* Update channel status to LIN_TX_HEADER_ERROR */
          LpRamVars->enChannelStatus = LIN_TX_HEADER_ERROR;
        }
        #ifdef LIN_E_TIMEOUT_FAILURE
        /* Report Error to DEM */
        Dem_ReportErrorStatus(LIN_E_TIMEOUT_FAILURE, DEM_EVENT_STATUS_FAILED);
        #endif

        /* Clear FTER bit */
        LpLN3ChannelRegs->ucRLN3nLEST =
                         (LpLN3ChannelRegs->ucRLN3nLEST & LIN_TIMEOUT_CLEAR);
      }
      else
      {
        /* No action required */
      }

      /* Check for Bit error */
      if (LIN_BIT_ERROR ==
                  (uint8)(LpLN3ChannelRegs->ucRLN3nLEST & LIN_BIT_ERROR))
      {
         /* Check if header frame transmit successfully */
        if (LIN_HEADER_COMPLETE ==
                (uint8)(LpLN3ChannelRegs->ucRLN3nLST & LIN_HEADER_COMPLETE))
        {
          /* Update channel status to LIN_TX_ERROR */
          LpRamVars->enChannelStatus = LIN_TX_ERROR;
        }
        /* Check if header frame transmit not successful */
        else
        {
          /* Update channel status to LIN_TX_HEADER_ERROR */
          LpRamVars->enChannelStatus = LIN_TX_HEADER_ERROR;
        }

        /* Clear BER bit */
        LpLN3ChannelRegs->ucRLN3nLEST =
                              (LpLN3ChannelRegs->ucRLN3nLEST & LIN_BIT_CLEAR);
      }
      else
      {
        /* No action required */
      }

      /* Check for Header Transmission error */
      if (LIN_PBUS_ERROR ==
                      (uint8)(LpLN3ChannelRegs->ucRLN3nLEST & LIN_PBUS_ERROR))
      {
        /* Update channel status to LIN_TX_HEADER_ERROR */
        LpRamVars->enChannelStatus = LIN_TX_HEADER_ERROR;

        /* Clear PBER bit */
        LpLN3ChannelRegs->ucRLN3nLEST =
                          (LpLN3ChannelRegs->ucRLN3nLEST & LIN_PBUS_CLEAR);
      }
      else
      {
        /* No action is required */
      }

      /* Check for any framing error */
      if (LIN_FRAMING_ERROR ==
                   (uint8)(LpLN3ChannelRegs->ucRLN3nLEST & LIN_FRAMING_ERROR))
      {
        /* Update channel status to LIN_RX_ERROR */
        LpRamVars->enChannelStatus = LIN_RX_ERROR;

        /* Clear CKE bit */
        LpLN3ChannelRegs->ucRLN3nLEST =
                         (LpLN3ChannelRegs->ucRLN3nLEST & LIN_FRAMING_CLEAR);
      }
      else
      {
        /* No action required */
      }

      if (LIN_HEADER_COMPLETE ==
          (uint8)(LpLN3ChannelRegs->ucRLN3nLST & LIN_HEADER_COMPLETE))
      {
        /* Clear the header interrupt */
        LpLN3ChannelRegs->ucRLN3nLST = (LpLN3ChannelRegs->ucRLN3nLST &
                                                             LIN_HEADER_DONE);
      }
      else
      {
        /* No action required */
      }

      /* Disable RLIN3 Interrupt */
      Lin_HwScheduleInterrupt(LucChannel, LIN_INTERRUPT_DIS);
    }
  }
  else
  {
    /* No action required */
  }
}
#define LIN_STOP_SEC_CODE_FAST
#include "Lin_MemMap.h"

/*******************************************************************************
** Function Name      : Lin_HwLoadTxBuffer
**
** Service ID         : NA
**
** Description        : This service copies data to Tx buffer
**
** Sync/Async         : Synchronous
**
** Re-entrancy        : Non Re-entrant for the same channel
**
** Input Parameters   : LucChannel
**
** Output Parameters  : None
**
** Return parameter   : None
**
** Preconditions      : None
**
** Global Variables   : Lin_GpChannelConfig, Lin_GpChannelRamData,
**                      Lin_GpRLIN3Properties
**
** Functions invoked  : None
**
** Registers Used     : ucRLN3nLDBR
**
** Reference ID       : LIN_DDD_ACT_020, LIN_DDD_ACT_020_REG001
*******************************************************************************/
#define LIN_START_SEC_PRIVATE_CODE
#include "Lin_MemMap.h"
STATIC FUNC(void, LIN_PRIVATE_CODE) Lin_HwLoadTxBuffer(const uint8 LucChannel)
{
  volatile P2CONST(Lin_ChannelInfo, AUTOMATIC, LIN_PRIVATE_CONST)
                                                               LpLinChannelPtr;
  volatile P2CONST(Lin3_ChannelConfigType, AUTOMATIC, LIN_PRIVATE_CONST)
                                                               LpLin3ChannelPtr;
  volatile P2VAR(RLin3_UartRegs, AUTOMATIC, REGSPACE)LpLN3ChannelRegs;
  volatile P2VAR(Lin_RamData, AUTOMATIC, LIN_VAR)LpRamVars;
  volatile P2VAR(uint8, AUTOMATIC, LIN_VAR) volatile LpStartDataBuffer;
  uint8 LucIncrement;
  uint8 LucDataLength;

  /* Get the pointer to frame data */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpRamVars = &Lin_GpChannelRamData[LucChannel];
  /* END Msg(4:0491)-2 */

  /* Copy the data length to local variable */
  LucDataLength = LpRamVars->ucFrameLength;
  
  LpStartDataBuffer = &LpRamVars->aaFrameData[(uint32)LIN_ZERO];

  /* Get the address of LIN Channel structure */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpLinChannelPtr = &Lin_GpChannelConfig[LucChannel];
  /* END Msg(4:0491)-2 */

  /* MISRA Violation: START Msg(4:0491)-2 */
  LpLin3ChannelPtr =
                  &Lin_GpRLIN3Properties[LpLinChannelPtr->ucPropertiesIndex];
  /* END Msg(4:0491)-2 */

  /* Get the RLIN3 Channel Base Address */
  LpLN3ChannelRegs = LpLin3ChannelPtr->pLn3ChanlBaseAddress;

  /* Copy data to Tx Buffer */
  for (LucIncrement = LIN_ZERO; LucDataLength > LucIncrement; LucIncrement++)
  {
    LpLN3ChannelRegs->ucRLN3nLDBR[LucIncrement] = *LpStartDataBuffer;

    /* MISRA Violation: START Msg(4:0489)-1 */
    LpStartDataBuffer++;
    /* END Msg(4:0489)-1 */
  }
}
#define LIN_STOP_SEC_PRIVATE_CODE
#include "Lin_MemMap.h"

/*******************************************************************************
** Function Name      : Lin_HwWakeupOnGoing
**
** Service ID         : NA
**
** Description        : This service does wake-up processing
**
** Sync/Async         : Synchronous
**
** Re-entrancy        : Non Re-entrant for the same channel
**
** Input Parameters   : LucChannel
**
** Output Parameters  : None
**
** Return parameter   : None
**
** Preconditions      : None
**
** Global Variables   : Lin_GpChannelConfig, Lin_GpChannelRamData,
**                      Lin_GpRLIN3Properties
**
** Functions invoked  : Lin_HwScheduleInterrupt
**
** Registers Used     : ucRLN3nLST
**
** Reference ID       : LIN_DDD_ACT_021, LIN_DDD_ACT_021_GBL001, 
** Reference ID       : LIN_DDD_ACT_021_GBL002, LIN_DDD_ACT_021_REG001
*******************************************************************************/
#define LIN_START_SEC_CODE_FAST
#include "Lin_MemMap.h"
STATIC FUNC(void, LIN_CODE_FAST) Lin_HwWakeupOnGoing(const uint8 LucChannel)
{
  volatile P2CONST(Lin_ChannelInfo, AUTOMATIC, LIN_CONST)LpLinChannelPtr;
  volatile P2CONST(Lin3_ChannelConfigType, AUTOMATIC, LIN_CONST)
                                                             LpLin3ChannelPtr;
  volatile P2VAR(RLin3_UartRegs, AUTOMATIC, REGSPACE)LpLN3ChannelRegs;
  volatile P2VAR(Lin_RamData, AUTOMATIC, LIN_VAR)LpRamVars;

  /* Get the address of LIN Channel structure */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpLinChannelPtr = &Lin_GpChannelConfig[LucChannel];
  /* END Msg(4:0491)-2 */

  /* Get the pointer to channel RAM data */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpRamVars = &Lin_GpChannelRamData[LucChannel];
  /* END Msg(4:0491)-2 */

  /* MISRA Violation: START Msg(4:0491)-2 */
  LpLin3ChannelPtr =
                 &Lin_GpRLIN3Properties[LpLinChannelPtr->ucPropertiesIndex];
  /* END Msg(4:0491)-2 */

  /* Get the RLIN3 Channel Base Address */
  LpLN3ChannelRegs = LpLin3ChannelPtr->pLn3ChanlBaseAddress;

  /* Check for Wakeup Transmit Successfully */
  if (LIN_FRAME_COMPLETE ==
                 (uint8)(LpLN3ChannelRegs->ucRLN3nLST & LIN_FRAME_COMPLETE))
  {
    /* Clear the transmit interrupt */
    LpLN3ChannelRegs->ucRLN3nLST = (LpLN3ChannelRegs->ucRLN3nLST &
                                                          LIN_TRANSMIT_DONE);

    /* Set the channel status */
    LpRamVars->enChannelStatus = LIN_OPERATIONAL;
  }
  else
  {
    /* Reset the LinIf wake-up flag */
    LpRamVars->blWakeupCalled = LIN_FALSE;
  }

  /* Disable RLIN3 Interrupt */
  Lin_HwScheduleInterrupt(LucChannel, LIN_INTERRUPT_DIS);
}
#define LIN_STOP_SEC_CODE_FAST
#include "Lin_MemMap.h"

/*******************************************************************************
** Function Name      : Lin_HwInit
**
** Service ID         : none
**
** Description        : This service initializes all configured RLIN3 channels.
**
** Sync/Async         : Synchronous
**
** Re-entrancy        : Non-Reentrant
**
** Input Parameters   : LucChannel
**
** InOut Parameters   : None
**
** Output Parameters  : None
**
** Return Parameter   : Std_ReturnType
**
** Preconditions      : None
**
** Global Variables   : Lin_GpChannelConfig,
**                      Lin_GpRLIN3Properties
**
** Functions invoked  : Lin_HwSetMasterMode, Lin_HwScheduleInterrupt
**
** Registers Used     : ucRLN3nLWBR,
**                      ucRLN3nLBRP0,
**                      ucRLN3nLBRP1,
**                      ucRLN3nLMD,
**                      ucRLN3nLBFC,
**                      ucRLN3nLSC,
**                      ucRLN3nLIE,
**                      ucRLN3nLEDE,
**                      ucRLN3nLDFC
**
** Reference ID       : LIN_DDD_ACT_022, LIN_DDD_ACT_022_REG001, 
** Reference ID       : LIN_DDD_ACT_022_REG002, LIN_DDD_ACT_022_REG003, 
** Reference ID       : LIN_DDD_ACT_022_REG004, LIN_DDD_ACT_022_REG005, 
** Reference ID       : LIN_DDD_ACT_022_REG006, LIN_DDD_ACT_022_REG007, 
** Reference ID       : LIN_DDD_ACT_022_REG008, LIN_DDD_ACT_022_REG009, 
** Reference ID       : LIN_DDD_ACT_022_REG010, LIN_DDD_ACT_022_REG011
*******************************************************************************/
#define LIN_START_SEC_PRIVATE_CODE
#include "Lin_MemMap.h"
FUNC(Std_ReturnType, LIN_PRIVATE_CODE) Lin_HwInit(const uint8 LucChannel)
{
  volatile P2CONST(Lin_ChannelInfo, AUTOMATIC, LIN_PRIVATE_CONST)
                                                             LpLinChannelPtr;
  volatile P2CONST(Lin3_ChannelConfigType, AUTOMATIC, LIN_PRIVATE_CONST)
                                                             LpLin3ChannelPtr;
  volatile P2VAR(RLin3_UartRegs, AUTOMATIC, REGSPACE) LpLN3ChannelRegs;

  uint8 LucLWBR;
  
  Std_ReturnType LucReturnValue;

  /* Initialize the return value */
  /* MISRA Violation: START Msg(4:2982)-4 */
  LucReturnValue = E_NOT_OK;
  /* END Msg(4:2982)-4 */

  /* Get the address of Channel info structure */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpLinChannelPtr = &Lin_GpChannelConfig[LucChannel];
  /* END Msg(4:0491)-2 */

  /* Get the address of RLIN3 Channel structure */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpLin3ChannelPtr =
              &Lin_GpRLIN3Properties[LpLinChannelPtr->ucPropertiesIndex];
  /* END Msg(4:0491)-2 */

  /* Get the pointer to RLIN3 Channel Base Address */
  LpLN3ChannelRegs = LpLin3ChannelPtr->pLn3ChanlBaseAddress;

  /* Set RLIN3 to Reset Mode */
  LucReturnValue = 
            Lin_HwSetMasterMode(LucChannel, RLIN3_RESET_MODE, LIN_INIT_SID);

  /* Set Prescaler Clock Select */
  LucLWBR = (uint8)(LpLin3ChannelPtr->ucPrescalerClk_Select << LIN_ONE);

  /* Set Value for number of samples in 1 Bit time period  */
  LucLWBR |= (uint8)(LpLin3ChannelPtr->ucBitSamples << LIN_FOUR);

  if (E_OK == LucReturnValue)
  {
    /* Check for LIN protocol specification 1.3 */
    if (LIN_FALSE == LpLin3ChannelPtr->blLinSpec_Select)
    {
       /* Set Baud Rate Selector register for LIN specification 1.3 */
       /* MISRA Violation: START Msg(4:2985)-5 */
       LpLN3ChannelRegs->ucRLN3nLWBR = LucLWBR | LIN_RESET_VALUE;
       /* MISRA Violation: START Msg(4:2985)-5 */
    }
    else
    {
       /* Set Baud Rate Selector register for LIN specification 2.x */
       LpLN3ChannelRegs->ucRLN3nLWBR = LucLWBR | LIN_SET_VALUE;
    }
    
    /* Set Baud Rate Prescaler0 register */
    LpLN3ChannelRegs->ucRLN3nLBRP0 = LpLin3ChannelPtr->ucBaudRate;
    
    /* Set Baud Rate Prescaler1 register */
    LpLN3ChannelRegs->ucRLN3nLBRP1 = LIN_RESET_VALUE;
    
    /* Enable LIN & Disable UART Interrupt Enable Register */
    /* Rx/Tx/Status Interrupt are used */
    LpLN3ChannelRegs->ucRLN3nLMD = RLIN3_INTERRUPT_REG;
    
    /* Set System Clock */
    LpLN3ChannelRegs->ucRLN3nLMD = (LpLN3ChannelRegs->ucRLN3nLMD |
                                              LpLinChannelPtr->ucModReg);
    /* Break Field Configure */
    LpLN3ChannelRegs->ucRLN3nLBFC = LpLinChannelPtr->ucRLINBreakfieldwidth;
    
    /* Set Inter-byte Header & Response Space width */
    LpLN3ChannelRegs->ucRLN3nLSC = LpLinChannelPtr->ucRLINInterbytespace;
    
    /* Set RLIN3 Module to LIN Master mode */
    LpLN3ChannelRegs->ucRLN3nLMD = (LpLN3ChannelRegs->ucRLN3nLMD &
                                                        RLIN3_MASTER_MODE);
    
    /* Set Interrupt Enable Register */
    LpLN3ChannelRegs->ucRLN3nLIE = LIN_ENABLE_INTERRUPT_REG;
    
    /* Set Error Detection Enable Register */
    LpLN3ChannelRegs->ucRLN3nLEDE = LIN_ERROR_INTERRUPT;
    
    /* Set LIN for Frame Mode */
    LpLN3ChannelRegs->ucRLN3nLDFC = (LpLN3ChannelRegs->ucRLN3nLDFC &
                                                           LIN_FRAME_MODE);
    
    /* Set RLIN3 to Operational Mode */
    LucReturnValue =
        Lin_HwSetMasterMode(LucChannel, RLIN3_OPERATION_MODE, LIN_INIT_SID);
  }
  else
  {
    /* No action required */
  }

  /* Disable RLIN3 Interrupt */
  Lin_HwScheduleInterrupt(LucChannel, LIN_INTERRUPT_DIS);

  return(LucReturnValue);
}
#define LIN_STOP_SEC_PRIVATE_CODE
#include "Lin_MemMap.h"

/*******************************************************************************
** Function Name      : Lin_HwSetMasterMode
**
** Service ID         : NA
**
** Description        : This service sets RLIN3 to the corresponding 
**                      Master Mode.
**                      It initiates a transition to the requested Master Mode.
**
** Sync/Async         : Synchronous
**
** Re-entrancy        : Non Re-entrant for the same channel
**
** Input Parameters   : LucChannel, LenModeType, LucApiId
**
** Output Parameters  : None
**
** Return parameter   : Std_ReturnType
**
** Preconditions      : None
**
** Global Variables   : Lin_GpChannelConfig, Lin_GpRLIN3Properties
**
** Functions invoked  : Lin_ErrorReport
**
** Registers Used     : ucRLN3nLCUC,
**                      ucRLN3nLMST
**
** Reference ID       : LIN_DDD_ACT_023, LIN_DDD_ACT_023_ERR001, 
** Reference ID       : LIN_DDD_ACT_023_REG001, LIN_DDD_ACT_023_REG002, 
** Reference ID       : LIN_DDD_ACT_023_REG003
*******************************************************************************/
#define LIN_START_SEC_CODE_FAST
#include "Lin_MemMap.h"
FUNC(Std_ReturnType, LIN_CODE_FAST) Lin_HwSetMasterMode(const uint8 LucChannel, 
                                    const Lin_ModeType LenModeType,
                                    const uint8 LucApiId)
{
  volatile P2CONST(Lin_ChannelInfo, AUTOMATIC, LIN_CONST)LpLinChannelPtr;
  volatile P2CONST(Lin3_ChannelConfigType, AUTOMATIC, LIN_CONST)
                                                          LpLin3ChannelPtr;
  volatile P2VAR(RLin3_UartRegs, AUTOMATIC, REGSPACE)LpLN3ChannelRegs;
  
  Std_ReturnType LucReturnValue;

  volatile uint32 LulTimeoutCounter;

  /* Initialize the return value */
  /* MISRA Violation: START Msg(4:2982)-4 */
  LucReturnValue = E_NOT_OK;
  /* END Msg(4:2982)-4 */

  /* Get the address of Channel info structure */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpLinChannelPtr = &Lin_GpChannelConfig[LucChannel];
  /* END Msg(4:0491)-2 */

  /* Get the address of RLIN3 Channel structure */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpLin3ChannelPtr =
                  &Lin_GpRLIN3Properties[LpLinChannelPtr->ucPropertiesIndex];
  /* END Msg(4:0491)-2 */

  /* Get the pointer to RLIN3 Channel Base Address */
  LpLN3ChannelRegs = LpLin3ChannelPtr->pLn3ChanlBaseAddress;

  /* Set max value of loop counter */
  LulTimeoutCounter = (uint32)LIN_TIMEOUT_DURATION;

  /* Change RLIN3 to the specified LucMode mode */
  switch (LenModeType)
  {
  case RLIN3_RESET_MODE:
    /* Set RLIN3 to Reset Mode */
    LpLN3ChannelRegs->ucRLN3nLCUC = LIN_RESET_VALUE;
    
    /* QAC Warning: START Msg(2:3416)-2 */
    if ((uint32)LIN_ZERO == LulTimeoutCounter)
    /* END Msg(2:3416)-2 */
    {
      if (LIN_RESET_VALUE ==
               (uint8)(LpLN3ChannelRegs->ucRLN3nLMST & LIN_SET_VALUE))
      {
         LulTimeoutCounter = (uint32)LIN_ONE;
      }
      else
      {
        /* No action required */
      }
    }
    else
    {
      /* After writing to bit[0], further write access to this register
         is not allowed until bit LiMST[0] is 0 */
      /* MISRA Violation: START Msg(4:3415)-3 */
      /* QAC Warning: START Msg(2:3416)-2 */
      while ((LIN_SET_VALUE ==
               (uint8)(LpLN3ChannelRegs->ucRLN3nLMST & LIN_SET_VALUE)) &&
                                          ((uint32)LIN_ZERO < LulTimeoutCounter))
      /* END Msg(2:3416)-2 */
      /* END Msg(4:3415)-3 */
      {
         LulTimeoutCounter--;
      }
    }
    break;
  case RLIN3_OPERATION_MODE:
    /* Set RLIN3 to Normal Mode*/
    LpLN3ChannelRegs->ucRLN3nLCUC = LIN_SET_OPERATION_MODE;
    
    /* QAC Warning: START Msg(2:3416)-2 */
    if ((uint32)LIN_ZERO == LulTimeoutCounter)
    /* END Msg(2:3416)-2 */
    {
      if (LIN_SET_OPERATION_MODE ==
        (uint8)(LpLN3ChannelRegs->ucRLN3nLMST & LIN_SET_OPERATION_MODE))
      {
        LulTimeoutCounter = (uint32)LIN_ONE;
      }
      else
      {
        /* No action required */
      }
    }
    else
    {
      /* After writing to bit[0], further write access to this register
         is not allowed until bit LiMST[0] is 0 */
      /* MISRA Violation: START Msg(4:3415)-3 */
      /* QAC Warning: START Msg(2:3416)-2 */
      while ((LIN_SET_OPERATION_MODE !=
      (uint8)(LpLN3ChannelRegs->ucRLN3nLMST & LIN_SET_OPERATION_MODE)) &&
                                  ((uint32)LIN_ZERO < LulTimeoutCounter))
      /* END Msg(2:3416)-2 */
      /* END Msg(4:3415)-3 */
      {
        LulTimeoutCounter--;
      }
    }
    break;
  case RLIN3_WAKEUP_MODE:
    /* Set RLIN3 to Wake-up Mode*/
    LpLN3ChannelRegs->ucRLN3nLCUC = LIN_SET_VALUE;
    
    /* QAC Warning: START Msg(2:3416)-2 */
    if ((uint32)LIN_ZERO == LulTimeoutCounter)
    /* END Msg(2:3416)-2 */
    {
      if (LIN_SET_VALUE ==
            (uint8)(LpLN3ChannelRegs->ucRLN3nLMST & LIN_SET_OPERATION_MODE))
      {
        LulTimeoutCounter = (uint32)LIN_ONE;
      }
      else
      {
        /* No action required */
      }
    }
    else
    {
      /* After writing 1 to bit[0], further write access to this register
         is not allowed until bit LMST[0] is 1 and bit LMST[1] is 0*/
      /* MISRA Violation: START Msg(4:3415)-3 */
      /* QAC Warning: START Msg(2:3416)-2 */
      while ((LIN_SET_VALUE !=
      (uint8)(LpLN3ChannelRegs->ucRLN3nLMST & LIN_SET_OPERATION_MODE)) &&
                                  ((uint32)LIN_ZERO < LulTimeoutCounter))
      /* END Msg(2:3416)-2 */
      /* END Msg(4:3415)-3 */
      {
        LulTimeoutCounter--;
      }
    }
    break;
  /* QAC Warning: START Msg(2:2016)-4 */
  default: 
    /* No action required */
    break;
  /* END Msg(2:2016)-4 */
  }

  #if defined (LIN_E_TIMEOUT) || (LIN_DEV_ERROR_DETECT == STD_ON)
  /* QAC Warning: START Msg(2:3416)-2 */
  if ((uint32)LIN_ZERO == LulTimeoutCounter)
  /* END Msg(2:3416)-2 */
  {
    /* Report Error to DEM or DET */
    LucReturnValue = Lin_ErrorReport(LucApiId);
  }
  else
  #endif /* LIN_E_TIMEOUT || (LIN_DEV_ERROR_DETECT == STD_ON) */
  {
    LucReturnValue = E_OK;
  }

  return (LucReturnValue);
}
#define LIN_STOP_SEC_CODE_FAST
#include "Lin_MemMap.h"

/*******************************************************************************
** Function Name      : Lin_HwScheduleInterrupt
**
** Service ID         : none
**
** Description        : This service enables or disables the interrupts 
**                      (transmission, reception and error status) of the 
**                      requested RLIN3 channel, using the EIMK bit of the 
**                      EIC register.
**
** Sync/Async         : Synchronous
**
** Re-entrancy        : Non-Reentrant for the same channel
**
** Input Parameters   : LucChannel, LenInterruptScheduleType
**
** InOut Parameters   : None
**
** Output Parameters  : None
**
** Return Parameter   : None
**
** Preconditions      : None
**
** Global Variables   : Lin_GpChannelConfig, Lin_GpRLIN3Properties
**
** Functions invoked  : None
**
** Registers Used     : pLin3IntTxEicReg,
**                      pLin3IntRxEicReg,
*                       pLin3IntStEicReg
**
** Reference ID       : LIN_DDD_ACT_024, LIN_DDD_ACT_024_REG001, 
** Reference ID       : LIN_DDD_ACT_024_REG002, LIN_DDD_ACT_024_REG003, 
** Reference ID       : LIN_DDD_ACT_024_REG004, LIN_DDD_ACT_024_REG005, 
** Reference ID       : LIN_DDD_ACT_024_REG006
*******************************************************************************/
#define LIN_START_SEC_CODE_FAST
#include "Lin_MemMap.h"
FUNC(void, LIN_CODE_FAST) Lin_HwScheduleInterrupt(const uint8 LucChannel, 
                      const Lin_InterruptScheduleType LenInterruptScheduleType)
{
  volatile P2CONST(Lin_ChannelInfo, AUTOMATIC, LIN_CONST)
                                                             LpLinChannelPtr;
  volatile P2CONST(Lin3_ChannelConfigType, AUTOMATIC, LIN_CONST)
                                                             LpLin3ChannelPtr;

  /* Get the address of Channel info structure */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpLinChannelPtr = &Lin_GpChannelConfig[LucChannel];
  /* END Msg(4:0491)-2 */

  /* Get the address of RLIN3 Channel structure */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpLin3ChannelPtr =
                  &Lin_GpRLIN3Properties[LpLinChannelPtr->ucPropertiesIndex];
  /* END Msg(4:0491)-2 */

  switch (LenInterruptScheduleType)
  {
  /* Disable RLIN3 Tx/Rx/St Interrupts */
  case LIN_INTERRUPT_DIS:
    /* Disable Interrupts, set the EIMKn bit of the EICn register */
    RH850_SV_MODE_ICR_OR(16, LpLin3ChannelPtr->pLin3IntTxEicReg,
                                                     LIN_EIC_EIMK_MASK);
    RH850_SV_MODE_ICR_OR(16, LpLin3ChannelPtr->pLin3IntRxEicReg,
                                                     LIN_EIC_EIMK_MASK);
    RH850_SV_MODE_ICR_OR(16, LpLin3ChannelPtr->pLin3IntStEicReg,
                                                     LIN_EIC_EIMK_MASK);
    /* Dummy read */
    RH850_SV_MODE_REG_READ_ONLY(16, LpLin3ChannelPtr->pLin3IntStEicReg);
    /* SYNCP execution */
    EXECUTE_SYNCP();
    break;
  /* Enable RLIN3 Tx/Rx/St Interrupts */
  case LIN_INTERRUPT_EN:
    /* Enable Interrupts, clear the EIMKn bit of the EICn register
       and clear pending interrupts */
    RH850_SV_MODE_ICR_AND(16, LpLin3ChannelPtr->pLin3IntTxEicReg,
                                               LIN_EIC_EIMK_MASK_CLEAR);
    RH850_SV_MODE_ICR_AND(16, LpLin3ChannelPtr->pLin3IntRxEicReg,
                                               LIN_EIC_EIMK_MASK_CLEAR);
    RH850_SV_MODE_ICR_AND(16, LpLin3ChannelPtr->pLin3IntStEicReg,
                                               LIN_EIC_EIMK_MASK_CLEAR);
    /* Dummy read */
    RH850_SV_MODE_REG_READ_ONLY(16, LpLin3ChannelPtr->pLin3IntStEicReg);
    /* SYNCP execution */
    EXECUTE_SYNCP();
    break;
  /* QAC Warning: START Msg(2:2016)-4 */
  default: 
    /* No action required */
    break;
  /* END Msg(2:2016)-4 */
  }
}
#define LIN_STOP_SEC_CODE_FAST
#include "Lin_MemMap.h"

/*******************************************************************************
** Function Name      : Lin_HwHandleWakeupRequest
**
** Service ID         : NA
**
** Sync/Async         : Synchronous
**
** Description        : This service enables a wake-up detection from the 
**                      LIN bus or issues a wake-up command to the LIN bus 
**                      according to the wake-up requested.
**
** Re-entrancy        : Non Re-entrant for the same channel
**
** Input Parameters   : LucChannel, LenWakeupType, LucApiId
**
** Output Parameters  : None
**
** Return parameter   : Std_ReturnType
**
** Preconditions      : None
**
** Global Variables   : Lin_GpChannelConfig, Lin_GpRLIN3Properties
**
** Functions invoked  : Lin_HwSetMasterMode, Lin_HwScheduleInterrupt
**
** Registers Used     : ucRLN3nLWUP,
**                      ucRLN3nLDFC,
**                      ucRLN3nLTRC
**
** Reference ID       : LIN_DDD_ACT_025, LIN_DDD_ACT_025_REG001, 
** Reference ID       : LIN_DDD_ACT_025_REG002, LIN_DDD_ACT_025_REG003, 
** Reference ID       : LIN_DDD_ACT_025_REG004
*******************************************************************************/
#define LIN_START_SEC_CODE_FAST
#include "Lin_MemMap.h"
FUNC(Std_ReturnType, LIN_CODE_FAST) Lin_HwHandleWakeupRequest(
                                     const uint8 LucChannel,
                                     const Lin_WakeupType LenWakeupType,
                                     const uint8 LucApiId)
{
  volatile P2CONST(Lin_ChannelInfo, AUTOMATIC, LIN_CONST)LpLinChannelPtr;
  volatile P2CONST(Lin3_ChannelConfigType, AUTOMATIC, LIN_CONST)
                                                             LpLin3ChannelPtr;
  volatile P2VAR(RLin3_UartRegs, AUTOMATIC, REGSPACE)LpLN3ChannelRegs;
  Std_ReturnType LucReturnValue;

  /* Initialize the return value */
  /* MISRA Violation: START Msg(4:2982)-4 */
  LucReturnValue = E_NOT_OK;
  /* END Msg(4:2982)-4 */

  /* Get the address of Channel info structure */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpLinChannelPtr = &Lin_GpChannelConfig[LucChannel];
  /* END Msg(4:0491)-2 */

  /* Get the address of RLIN3 Channel structure */
  /* MISRA Violation: START Msg(4:0491)-2 */
  LpLin3ChannelPtr =
                  &Lin_GpRLIN3Properties[LpLinChannelPtr->ucPropertiesIndex];
  /* END Msg(4:0491)-2 */

  /* Get the pointer to RLIN3 Channel Base Address */
  LpLN3ChannelRegs = LpLin3ChannelPtr->pLn3ChanlBaseAddress;

  /* Set RLIN3 to Reset Mode */
  LucReturnValue = Lin_HwSetMasterMode(LucChannel, RLIN3_RESET_MODE, LucApiId);

  if (E_OK == LucReturnValue)
  {
    /* Set RLIN3 Wake-up Low width */
    LpLN3ChannelRegs->ucRLN3nLWUP = LIN_LOW_WIDTH;

    switch (LenWakeupType)
    {
    case LIN_SLAVE_ISSUE:
      /* Set the Direction of RLIN3 as Reception */
      LpLN3ChannelRegs->ucRLN3nLDFC = (LpLN3ChannelRegs->ucRLN3nLDFC &
                                                    LIN_SLAVE_DIRECTION);
      break;
    case LIN_MASTER_ISSUE:
      /* Set the Direction of RLIN3 as Transmit */
      LpLN3ChannelRegs->ucRLN3nLDFC = (LpLN3ChannelRegs->ucRLN3nLDFC |
                                                   LIN_MASTER_DIRECTION);
      break;
    /* QAC Warning: START Msg(2:2016)-4 */
    default: 
      /* No action required */
      break;
    /* END Msg(2:2016)-4 */
    }

    /* Set RLIN3 to Wake-up Mode */
    LucReturnValue = Lin_HwSetMasterMode(LucChannel, 
                                                  RLIN3_WAKEUP_MODE, LucApiId);
  }
  else
  {
    /* No action required */
  }

  if (E_OK == LucReturnValue)
  {
    /* Enable RLIN3 Interrupt */
    Lin_HwScheduleInterrupt(LucChannel, LIN_INTERRUPT_EN);
    
    /* Initiate Communication */
    LpLN3ChannelRegs->ucRLN3nLTRC = (LpLN3ChannelRegs->ucRLN3nLTRC |
                                                    LIN_START_TRANSMIT);
  }
  else
  {
    /* No action required */
  }
  return (LucReturnValue);
}
#define LIN_STOP_SEC_CODE_FAST
#include "Lin_MemMap.h"

/*******************************************************************************
** Function Name      : Lin_ErrorReport
**
** Service ID         : NA
**
** Sync/Async         : Synchronous
**
** Description        : This service reports an error to DEM or DET if a 
**                      loop-processing-time-out error occurs
**
** Re-entrancy        : Re-entrant
**
** Input Parameters   : LulIndex, LucApiId
**
** Output Parameters  : None
**
** Return parameter   : Std_ReturnType
**
** Preconditions      : None
**
** Global Variables   : None
**
** Functions invoked  : Det_ReportError, Dem_ReportErrorStatus
**
** Registers Used     : None
**
** Reference ID       : LIN_DDD_ACT_026, LIN_DDD_ACT_026_ERR001
*******************************************************************************/
#define LIN_START_SEC_CODE_FAST
#include "Lin_MemMap.h"
#if defined (LIN_E_TIMEOUT) || (LIN_DEV_ERROR_DETECT == STD_ON)
/* QAC Warning: START Msg(2:3206)-5 */
STATIC FUNC(Std_ReturnType, LIN_CODE_FAST) Lin_ErrorReport(
                                         const uint8 LucApiId)
/* QAC Warning: START Msg(2:3206)-5 */
{
  Std_ReturnType LucReturnValue;

  /* Initialize the return value */
  /* MISRA Violation: START Msg(4:2982)-4 */
  LucReturnValue = E_OK;
  /* END Msg(4:2982)-4 */

  #ifdef LIN_E_TIMEOUT
    /* Report Error to DEM */
    Dem_ReportErrorStatus(LIN_E_TIMEOUT, DEM_EVENT_STATUS_FAILED);
    LucReturnValue = E_NOT_OK;
  #else
  #if (LIN_DEV_ERROR_DETECT == STD_ON)
    /* Report to DET */
    (void)Det_ReportError(LIN_MODULE_ID, LIN_INSTANCE_ID,
                                   LucApiId, LIN_E_DET_TIMEOUT);
    LucReturnValue = E_NOT_OK;
  #endif /* (LIN_DEV_ERROR_DETECT == STD_ON) */
  #endif /* LIN_E_TIMEOUT */

  return (LucReturnValue);
}
#endif /* LIN_E_TIMEOUT || (LIN_DEV_ERROR_DETECT == STD_ON) */
#define LIN_STOP_SEC_CODE_FAST
#include "Lin_MemMap.h"

/*******************************************************************************
** Function Name      : Lin_SetStatus
**
** Service ID         : none
**
** Description        : This function updates the status of Lin_GblDriverState.
**                      The purpose of this function is to prevent the order of
**                      instructions being changed by the compiler.
**
** Sync/Async         : Synchronous
**
** Re-entrancy        : Non-Reentrant
**
** Input Parameters   : LblStatus
**
** InOut Parameters   : None
**
** Output Parameters  : None
**
** Return Parameter   : None
**
** Preconditions      : None
**
** Global Variables   : Lin_GblDriverState
**
** Functions invoked  : None
**
** Registers Used     : None
**
** Reference ID       : LIN_DDD_ACT_027, LIN_DDD_ACT_027_GBL001
*******************************************************************************/
#define LIN_START_SEC_PRIVATE_CODE
#include "Lin_MemMap.h"
FUNC(void, LIN_PRIVATE_CODE) Lin_SetStatus(const boolean LblStatus)
{
  /* Set Lin_GblDriverState to LblStatus */
  Lin_GblDriverState = LblStatus;
}
#define LIN_STOP_SEC_PRIVATE_CODE
#include "Lin_MemMap.h"

/*******************************************************************************
**                          End of File                                       **
*******************************************************************************/
