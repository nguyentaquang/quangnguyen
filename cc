/*============================================================================*/
/* Project      = AUTOSAR Renesas X2x MCAL Components                         */
/* Module       = Lin_PBTypes.h                                               */
/* Version      = V1.0.0                                                      */
/* Date         = 03-Dec-2019                                                 */
/* Reference    = LIN_DFD_DTT_002                                             */
/* Reference    = LIN_DFD_DTT_003                                             */
/* Reference    = LIN_DFD_DTT_004                                             */
/* Reference    = LIN_DFD_DTT_005                                             */
/*============================================================================*/
/*                                  COPYRIGHT                                 */
/*============================================================================*/
/* (c) 2019 Renesas Electronics Corporation. All rights reserved.             */
/*============================================================================*/
/* Purpose:                                                                   */
/* This file contains the type definitions of Post-build Time Parameters      */
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
 * V1.0.0:  03-Dec-2019  : Initial Version
 */
/******************************************************************************/

#ifndef LIN_PBTYPES_H
#define LIN_PBTYPES_H

/*******************************************************************************
**                      Include Section                                       **
*******************************************************************************/
/* Included for Lin type declarations */
#include "Lin_Types.h"
/* Included for Lin general type declarations */
#include "Lin_GeneralTypes.h"
/* Included for declarations of EcuM APIs */
#if (LIN_WAKEUP_SUPPORT == STD_ON)
#include "EcuM.h"
#endif

/*******************************************************************************
**                      MISRA C Rule Violations                               **
*******************************************************************************/
/* 1. MISRA C RULE VIOLATION:                                                 */
/* Message       : (4:3684) Array declared with unknown size.                 */
/* Rule          : MISRA-C:2004 Rule 8.12                                     */
/* Justification : Subscripting cannot be applied on the array since size can */
/*                 grow based on configuration done by user i.e. multi        */
/*                 configuration                                              */
/* Verification  : However, part of the code is verified manually and it is   */
/*                 not having any impact.                                     */
/* Reference     : Look for START Msg(4:3684)-1 and                           */
/*                 END Msg(4:3684)-1 tags in the code.                        */

/******************************************************************************/

/*******************************************************************************
**                      Global Symbols                                        **
*******************************************************************************/
/* AUTOSAR Release version information */
#define LIN_PBTYPES_AR_RELEASE_MAJOR_VERSION  LIN_TYPES_AR_RELEASE_MAJOR_VERSION
#define LIN_PBTYPES_AR_RELEASE_MINOR_VERSION  LIN_TYPES_AR_RELEASE_MINOR_VERSION
#define LIN_PBTYPES_AR_RELEASE_REVISION_VERSION  \
                                           LIN_TYPES_AR_RELEASE_REVISION_VERSION

/* Module Software version information */
#define LIN_PBTYPES_SW_MAJOR_VERSION   LIN_TYPES_SW_MAJOR_VERSION
#define LIN_PBTYPES_SW_MINOR_VERSION   LIN_TYPES_SW_MINOR_VERSION

#define LIN_DBTOC_VALUE           (((uint32)LIN_VENDOR_ID_VALUE << 22) | \
                                  ((uint32)LIN_MODULE_ID_VALUE << 14) | \
                                  ((uint32)LIN_SW_MAJOR_VERSION_VALUE << 8) | \
                                  ((uint32)LIN_SW_MINOR_VERSION_VALUE << 3))

#if (LIN_CRITICAL_SECTION_PROTECTION == STD_ON)
#define LIN_ENTER_CRITICAL_SECTION(Exclusive_Area) \
                                              SchM_Enter_Lin_##Exclusive_Area()

#define LIN_EXIT_CRITICAL_SECTION(Exclusive_Area) \
                                               SchM_Exit_Lin_##Exclusive_Area()
#else
#define LIN_ENTER_CRITICAL_SECTION(Exclusive_Area) 
#define LIN_EXIT_CRITICAL_SECTION(Exclusive_Area) 
#endif /* #if (LIN_CRITICAL_SECTION_PROTECTION == STD_ON) */

/******************************************************************************/


/*******************************************************************************
**                      Global Data Types                                     **
*******************************************************************************/
#define LIN_REG_RESERVED_SIZE_3BYTES              (uint8)0x03
#define LIN_REG_RESERVED_SIZE_8BYTES              (uint8)0x08
#define LIN_FRAMEDATA_SIZE                        (uint8)0x08

/* Structure for LIN Channel RAM data */
typedef struct STag_Lin_RamData
{
  Lin_FrameResponseType enFrameType;
  /* To hold  8 byte frame data + 1 byte checksum */
  uint8 aaFrameData[LIN_FRAMEDATA_SIZE];
  Lin_FramePidType ucFrameId;
  Lin_FrameDlType ucFrameLength;
  Lin_FrameCsModelType enCheckSumModel;
  Lin_StatusType enChannelStatus;
  uint8 ucSlpRqst_RespRdy;
  boolean blWakeupCalled;
  boolean blSleepPending;
} Lin_RamData;

/* Structure for  RLIN3 hardware Channel registers */
typedef struct STag_RLin3_UartRegs
{
  uint8 volatile ucRLN3nLWBR;
  uint8 volatile ucRLN3nLBRP0;
  uint8 volatile ucRLN3nLBRP1;
  uint8 volatile ucRLN3nLSTC;
  uint8 volatile ucReserved1[LIN_REG_RESERVED_SIZE_3BYTES];
  uint8 volatile ucRLN3nLMD;
  uint8 volatile ucRLN3nLBFC;
  uint8 volatile ucRLN3nLSC;
  uint8 volatile ucRLN3nLWUP;
  uint8 volatile ucRLN3nLIE;
  uint8 volatile ucRLN3nLEDE;
  uint8 volatile ucRLN3nLCUC;
  uint8 volatile ucReserved2;
  uint8 volatile ucRLN3nLTRC;
  uint8 volatile ucRLN3nLMST;
  uint8 volatile ucRLN3nLST;
  uint8 volatile ucRLN3nLEST;
  uint8 volatile ucRLN3nLDFC;
  uint8 volatile ucRLN3nLIDB;
  uint8 volatile ucRLN3nLCBR;
  uint8 volatile ucReserved3;
  uint8 volatile ucRLN3nLDBR[LIN_REG_RESERVED_SIZE_8BYTES];
} RLin3_UartRegs;

/* Structure for RLIN3 Channel information */
typedef struct STag_Lin3_ChannelConfigType
{
  volatile P2VAR(RLin3_UartRegs, TYPEDEF, REGSPACE)pLn3ChanlBaseAddress;
  uint8 ucBaudRate;
  uint8 ucPrescalerClk_Select;
  uint8 ucBitSamples;
  boolean blLinSpec_Select;
  volatile P2VAR(uint16, TYPEDEF, REGSPACE)pLin3IntTxEicReg;
  volatile P2VAR(uint16, TYPEDEF, REGSPACE)pLin3IntRxEicReg;
  volatile P2VAR(uint16, TYPEDEF, REGSPACE)pLin3IntStEicReg;
} Lin3_ChannelConfigType;

typedef struct STag_Lin_ChannelInfo
{
  #if (LIN_WAKEUP_SUPPORT == STD_ON)
  uint8 ucWakeupSourceId;
  #endif
  uint8 ucModReg;
  /*
   * Bit [3:0] = index of LIN3 channel properties.
   */
  uint8 ucPropertiesIndex;
  #if (LIN_WAKEUP_SUPPORT == STD_ON)
  boolean blWakeupSupport;
  #endif

  /* Break Field Config Value */
  uint8 ucRLINBreakfieldwidth;
  /* Inter-Byte Space Config Value */
  uint8 ucRLINInterbytespace;
} Lin_ChannelInfo;

/*******************************************************************************
**                      Global Data                                           **
*******************************************************************************/
#define LIN_START_SEC_CONFIG_DATA_UNSPECIFIED
#include "Lin_MemMap.h"

/* This array contains RLIN3 channel specific information for all active
   channels */
/* MISRA Violation: START Msg(4:3684)-1 */
extern CONST(Lin3_ChannelConfigType, LIN_APPL_CONST) Lin_GaaRLIN3Properties[];
/* END Msg(4:3684)-1 */

/* This array contains channel specific information for all active channels */
/* MISRA Violation: START Msg(4:3684)-1 */
extern CONST(Lin_ChannelInfo, LIN_APPL_CONST)Lin_GaaChannelConfig[];
/* END Msg(4:3684)-1 */

#define LIN_STOP_SEC_CONFIG_DATA_UNSPECIFIED
#include "Lin_MemMap.h"

#define LIN_START_SEC_VAR_NO_INIT_UNSPECIFIED
#include "Lin_MemMap.h"

/* RAM allocation for all active channels */
/* MISRA Violation: START Msg(4:3684)-1 */
extern volatile VAR(Lin_RamData, LIN_NOINIT_DATA) Lin_GaaChannelRamData[];
/* END Msg(4:3684)-1 */

#define LIN_STOP_SEC_VAR_NO_INIT_UNSPECIFIED
#include "Lin_MemMap.h"
/*******************************************************************************
**                      Function Prototypes                                   **
*******************************************************************************/

#endif /* LIN_PBTYPES_H */

/*******************************************************************************
**                          End of File                                       **
*******************************************************************************/
