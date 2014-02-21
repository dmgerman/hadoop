begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/** Utility to assist with generation of progress reports.  Applications build  * a hierarchy of {@link Progress} instances, each modelling a phase of  * execution.  The root is constructed with {@link #Progress()}.  Nodes for  * sub-phases are created by calling {@link #addPhase()}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|Progress
specifier|public
class|class
name|Progress
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|Progress
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|status
specifier|private
name|String
name|status
init|=
literal|""
decl_stmt|;
DECL|field|progress
specifier|private
name|float
name|progress
decl_stmt|;
DECL|field|currentPhase
specifier|private
name|int
name|currentPhase
decl_stmt|;
DECL|field|phases
specifier|private
name|ArrayList
argument_list|<
name|Progress
argument_list|>
name|phases
init|=
operator|new
name|ArrayList
argument_list|<
name|Progress
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|parent
specifier|private
name|Progress
name|parent
decl_stmt|;
comment|// Each phase can have different progress weightage. For example, in
comment|// Map Task, map phase accounts for 66.7% and sort phase for 33.3%.
comment|// User needs to give weightages as parameters to all phases(when adding
comment|// phases) in a Progress object, if he wants to give weightage to any of the
comment|// phases. So when nodes are added without specifying weightage, it means
comment|// fixed weightage for all phases.
DECL|field|fixedWeightageForAllPhases
specifier|private
name|boolean
name|fixedWeightageForAllPhases
init|=
literal|false
decl_stmt|;
DECL|field|progressPerPhase
specifier|private
name|float
name|progressPerPhase
init|=
literal|0.0f
decl_stmt|;
DECL|field|progressWeightagesForPhases
specifier|private
name|ArrayList
argument_list|<
name|Float
argument_list|>
name|progressWeightagesForPhases
init|=
operator|new
name|ArrayList
argument_list|<
name|Float
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Creates a new root node. */
DECL|method|Progress ()
specifier|public
name|Progress
parameter_list|()
block|{}
comment|/** Adds a named node to the tree. */
DECL|method|addPhase (String status)
specifier|public
name|Progress
name|addPhase
parameter_list|(
name|String
name|status
parameter_list|)
block|{
name|Progress
name|phase
init|=
name|addPhase
argument_list|()
decl_stmt|;
name|phase
operator|.
name|setStatus
argument_list|(
name|status
argument_list|)
expr_stmt|;
return|return
name|phase
return|;
block|}
comment|/** Adds a node to the tree. Gives equal weightage to all phases */
DECL|method|addPhase ()
specifier|public
specifier|synchronized
name|Progress
name|addPhase
parameter_list|()
block|{
name|Progress
name|phase
init|=
name|addNewPhase
argument_list|()
decl_stmt|;
comment|// set equal weightage for all phases
name|progressPerPhase
operator|=
literal|1.0f
operator|/
operator|(
name|float
operator|)
name|phases
operator|.
name|size
argument_list|()
expr_stmt|;
name|fixedWeightageForAllPhases
operator|=
literal|true
expr_stmt|;
return|return
name|phase
return|;
block|}
comment|/** Adds a new phase. Caller needs to set progress weightage */
DECL|method|addNewPhase ()
specifier|private
specifier|synchronized
name|Progress
name|addNewPhase
parameter_list|()
block|{
name|Progress
name|phase
init|=
operator|new
name|Progress
argument_list|()
decl_stmt|;
name|phases
operator|.
name|add
argument_list|(
name|phase
argument_list|)
expr_stmt|;
name|phase
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
name|phase
return|;
block|}
comment|/** Adds a named node with a specified progress weightage to the tree. */
DECL|method|addPhase (String status, float weightage)
specifier|public
name|Progress
name|addPhase
parameter_list|(
name|String
name|status
parameter_list|,
name|float
name|weightage
parameter_list|)
block|{
name|Progress
name|phase
init|=
name|addPhase
argument_list|(
name|weightage
argument_list|)
decl_stmt|;
name|phase
operator|.
name|setStatus
argument_list|(
name|status
argument_list|)
expr_stmt|;
return|return
name|phase
return|;
block|}
comment|/** Adds a node with a specified progress weightage to the tree. */
DECL|method|addPhase (float weightage)
specifier|public
specifier|synchronized
name|Progress
name|addPhase
parameter_list|(
name|float
name|weightage
parameter_list|)
block|{
name|Progress
name|phase
init|=
operator|new
name|Progress
argument_list|()
decl_stmt|;
name|progressWeightagesForPhases
operator|.
name|add
argument_list|(
name|weightage
argument_list|)
expr_stmt|;
name|phases
operator|.
name|add
argument_list|(
name|phase
argument_list|)
expr_stmt|;
name|phase
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// Ensure that the sum of weightages does not cross 1.0
name|float
name|sum
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|phases
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|sum
operator|+=
name|progressWeightagesForPhases
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sum
operator|>
literal|1.0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Sum of weightages can not be more than 1.0; But sum = "
operator|+
name|sum
argument_list|)
expr_stmt|;
block|}
return|return
name|phase
return|;
block|}
comment|/** Adds n nodes to the tree. Gives equal weightage to all phases */
DECL|method|addPhases (int n)
specifier|public
specifier|synchronized
name|void
name|addPhases
parameter_list|(
name|int
name|n
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|addNewPhase
argument_list|()
expr_stmt|;
block|}
comment|// set equal weightage for all phases
name|progressPerPhase
operator|=
literal|1.0f
operator|/
operator|(
name|float
operator|)
name|phases
operator|.
name|size
argument_list|()
expr_stmt|;
name|fixedWeightageForAllPhases
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * returns progress weightage of the given phase    * @param phaseNum the phase number of the phase(child node) for which we need    *                 progress weightage    * @return returns the progress weightage of the specified phase    */
DECL|method|getProgressWeightage (int phaseNum)
name|float
name|getProgressWeightage
parameter_list|(
name|int
name|phaseNum
parameter_list|)
block|{
if|if
condition|(
name|fixedWeightageForAllPhases
condition|)
block|{
return|return
name|progressPerPhase
return|;
comment|// all phases are of equal weightage
block|}
return|return
name|progressWeightagesForPhases
operator|.
name|get
argument_list|(
name|phaseNum
argument_list|)
return|;
block|}
DECL|method|getParent ()
specifier|synchronized
name|Progress
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
DECL|method|setParent (Progress parent)
specifier|synchronized
name|void
name|setParent
parameter_list|(
name|Progress
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
comment|/** Called during execution to move to the next phase at this level in the    * tree. */
DECL|method|startNextPhase ()
specifier|public
specifier|synchronized
name|void
name|startNextPhase
parameter_list|()
block|{
name|currentPhase
operator|++
expr_stmt|;
block|}
comment|/** Returns the current sub-node executing. */
DECL|method|phase ()
specifier|public
specifier|synchronized
name|Progress
name|phase
parameter_list|()
block|{
return|return
name|phases
operator|.
name|get
argument_list|(
name|currentPhase
argument_list|)
return|;
block|}
comment|/** Completes this node, moving the parent node to its next child. */
DECL|method|complete ()
specifier|public
name|void
name|complete
parameter_list|()
block|{
comment|// we have to traverse up to our parent, so be careful about locking.
name|Progress
name|myParent
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|progress
operator|=
literal|1.0f
expr_stmt|;
name|myParent
operator|=
name|parent
expr_stmt|;
block|}
if|if
condition|(
name|myParent
operator|!=
literal|null
condition|)
block|{
comment|// this will synchronize on the parent, so we make sure we release
comment|// our lock before getting the parent's, since we're traversing
comment|// against the normal traversal direction used by get() or toString().
comment|// We don't need transactional semantics, so we're OK doing this.
name|myParent
operator|.
name|startNextPhase
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Called during execution on a leaf node to set its progress. */
DECL|method|set (float progress)
specifier|public
specifier|synchronized
name|void
name|set
parameter_list|(
name|float
name|progress
parameter_list|)
block|{
if|if
condition|(
name|Float
operator|.
name|isNaN
argument_list|(
name|progress
argument_list|)
condition|)
block|{
name|progress
operator|=
literal|0
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Illegal progress value found, progress is Float.NaN. "
operator|+
literal|"Progress will be changed to 0"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|progress
operator|==
name|Float
operator|.
name|NEGATIVE_INFINITY
condition|)
block|{
name|progress
operator|=
literal|0
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Illegal progress value found, progress is "
operator|+
literal|"Float.NEGATIVE_INFINITY. Progress will be changed to 0"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|progress
operator|<
literal|0
condition|)
block|{
name|progress
operator|=
literal|0
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Illegal progress value found, progress is less than 0."
operator|+
literal|" Progress will be changed to 0"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|progress
operator|>
literal|1
condition|)
block|{
name|progress
operator|=
literal|1
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Illegal progress value found, progress is larger than 1."
operator|+
literal|" Progress will be changed to 1"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|progress
operator|==
name|Float
operator|.
name|POSITIVE_INFINITY
condition|)
block|{
name|progress
operator|=
literal|1
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Illegal progress value found, progress is "
operator|+
literal|"Float.POSITIVE_INFINITY. Progress will be changed to 1"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|progress
operator|=
name|progress
expr_stmt|;
block|}
comment|/** Returns the overall progress of the root. */
comment|// this method probably does not need to be synchronized as getInternal() is
comment|// synchronized and the node's parent never changes. Still, it doesn't hurt.
DECL|method|get ()
specifier|public
specifier|synchronized
name|float
name|get
parameter_list|()
block|{
name|Progress
name|node
init|=
name|this
decl_stmt|;
while|while
condition|(
name|node
operator|.
name|getParent
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// find the root
name|node
operator|=
name|parent
expr_stmt|;
block|}
return|return
name|node
operator|.
name|getInternal
argument_list|()
return|;
block|}
comment|/**    * Returns progress in this node. get() would give overall progress of the    * root node(not just given current node).    */
DECL|method|getProgress ()
specifier|public
specifier|synchronized
name|float
name|getProgress
parameter_list|()
block|{
return|return
name|getInternal
argument_list|()
return|;
block|}
comment|/** Computes progress in this node. */
DECL|method|getInternal ()
specifier|private
specifier|synchronized
name|float
name|getInternal
parameter_list|()
block|{
name|int
name|phaseCount
init|=
name|phases
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|phaseCount
operator|!=
literal|0
condition|)
block|{
name|float
name|subProgress
init|=
literal|0.0f
decl_stmt|;
name|float
name|progressFromCurrentPhase
init|=
literal|0.0f
decl_stmt|;
if|if
condition|(
name|currentPhase
operator|<
name|phaseCount
condition|)
block|{
name|subProgress
operator|=
name|phase
argument_list|()
operator|.
name|getInternal
argument_list|()
expr_stmt|;
name|progressFromCurrentPhase
operator|=
name|getProgressWeightage
argument_list|(
name|currentPhase
argument_list|)
operator|*
name|subProgress
expr_stmt|;
block|}
name|float
name|progressFromCompletedPhases
init|=
literal|0.0f
decl_stmt|;
if|if
condition|(
name|fixedWeightageForAllPhases
condition|)
block|{
comment|// same progress weightage for each phase
name|progressFromCompletedPhases
operator|=
name|progressPerPhase
operator|*
name|currentPhase
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|currentPhase
condition|;
name|i
operator|++
control|)
block|{
comment|// progress weightages of phases could be different. Add them
name|progressFromCompletedPhases
operator|+=
name|getProgressWeightage
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|progressFromCompletedPhases
operator|+
name|progressFromCurrentPhase
return|;
block|}
else|else
block|{
return|return
name|progress
return|;
block|}
block|}
DECL|method|setStatus (String status)
specifier|public
specifier|synchronized
name|void
name|setStatus
parameter_list|(
name|String
name|status
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|toString
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toString (StringBuilder buffer)
specifier|private
specifier|synchronized
name|void
name|toString
parameter_list|(
name|StringBuilder
name|buffer
parameter_list|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|status
argument_list|)
expr_stmt|;
if|if
condition|(
name|phases
operator|.
name|size
argument_list|()
operator|!=
literal|0
operator|&&
name|currentPhase
operator|<
name|phases
operator|.
name|size
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"> "
argument_list|)
expr_stmt|;
name|phase
argument_list|()
operator|.
name|toString
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

