begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|s3
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Ranger Header class which hold startoffset, endoffset of the Range header  * value provided as part of get object.  *  */
end_comment

begin_class
DECL|class|RangeHeader
specifier|public
class|class
name|RangeHeader
block|{
DECL|field|startOffset
specifier|private
name|long
name|startOffset
decl_stmt|;
DECL|field|endOffset
specifier|private
name|long
name|endOffset
decl_stmt|;
DECL|field|readFull
specifier|private
name|boolean
name|readFull
decl_stmt|;
DECL|field|inValidRange
specifier|private
name|boolean
name|inValidRange
decl_stmt|;
comment|/**    * Construct RangeHeader object.    * @param startOffset    * @param endOffset    */
DECL|method|RangeHeader (long startOffset, long endOffset, boolean full, boolean invalid)
specifier|public
name|RangeHeader
parameter_list|(
name|long
name|startOffset
parameter_list|,
name|long
name|endOffset
parameter_list|,
name|boolean
name|full
parameter_list|,
name|boolean
name|invalid
parameter_list|)
block|{
name|this
operator|.
name|startOffset
operator|=
name|startOffset
expr_stmt|;
name|this
operator|.
name|endOffset
operator|=
name|endOffset
expr_stmt|;
name|this
operator|.
name|readFull
operator|=
name|full
expr_stmt|;
name|this
operator|.
name|inValidRange
operator|=
name|invalid
expr_stmt|;
block|}
comment|/**    * Return startOffset.    *    * @return startOffset    */
DECL|method|getStartOffset ()
specifier|public
name|long
name|getStartOffset
parameter_list|()
block|{
return|return
name|startOffset
return|;
block|}
comment|/**    * Return endoffset.    *    * @return endoffset    */
DECL|method|getEndOffset ()
specifier|public
name|long
name|getEndOffset
parameter_list|()
block|{
return|return
name|endOffset
return|;
block|}
comment|/**    * Return a flag whether after parsing range header, when the provided    * values are with in a range, and whole file read is required.    *    * @return readFull    */
DECL|method|isReadFull ()
specifier|public
name|boolean
name|isReadFull
parameter_list|()
block|{
return|return
name|readFull
return|;
block|}
comment|/**    * Return a flag, whether range header values are correct or not.    *    * @return isInValidRange    */
DECL|method|isInValidRange ()
specifier|public
name|boolean
name|isInValidRange
parameter_list|()
block|{
return|return
name|inValidRange
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"startOffset - ["
operator|+
name|startOffset
operator|+
literal|"]"
operator|+
literal|"endOffset - ["
operator|+
name|endOffset
operator|+
literal|"]"
operator|+
literal|" readFull - [ "
operator|+
name|readFull
operator|+
literal|"]"
operator|+
literal|" invalidRange "
operator|+
literal|"- [ "
operator|+
name|inValidRange
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

