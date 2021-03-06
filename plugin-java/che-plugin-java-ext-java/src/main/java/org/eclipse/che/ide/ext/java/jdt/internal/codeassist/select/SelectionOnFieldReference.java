/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.java.jdt.internal.codeassist.select;

/*
 * Selection node build by the parser in any case it was intending to
 * reduce a field reference containing the cursor.
 * e.g.
 *
 *	class X {
 *    void foo() {
 *      bar().[start]fred[end]
 *    }
 *  }
 *
 *	---> class X {
 *         void foo() {
 *           <SelectOnFieldReference:bar().fred>
 *         }
 *       }
 *
 */

import org.eclipse.che.ide.ext.java.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.che.ide.ext.java.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.che.ide.ext.java.jdt.internal.compiler.lookup.ProblemReasons;
import org.eclipse.che.ide.ext.java.jdt.internal.compiler.lookup.TypeBinding;

public class SelectionOnFieldReference extends FieldReference {

	public SelectionOnFieldReference(char[] source , long pos) {

		super(source, pos);
	}

	public StringBuffer printExpression(int indent, StringBuffer output){

		output.append("<SelectionOnFieldReference:");  //$NON-NLS-1$
		return super.printExpression(0, output).append('>');
	}

	public TypeBinding resolveType(BlockScope scope) {

		super.resolveType(scope);
		// tolerate some error cases
		if (this.binding == null ||
				!(this.binding.isValidBinding() ||
					this.binding.problemId() == ProblemReasons.NotVisible
					|| this.binding.problemId() == ProblemReasons.InheritedNameHidesEnclosingName
					|| this.binding.problemId() == ProblemReasons.NonStaticReferenceInConstructorInvocation
					|| this.binding.problemId() == ProblemReasons.NonStaticReferenceInStaticContext))
			throw new SelectionNodeFound();
		else
			throw new SelectionNodeFound(this.binding);
	}
}
