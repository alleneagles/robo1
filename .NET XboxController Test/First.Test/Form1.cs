using J2i.Net.XInputWrapper;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace First.Test
{
    public partial class Form1 : Form
    {
        private System.Timers.Timer _pollingTimer;

        public Form1()
        {
            InitializeComponent();

            XboxController.StartPolling();

            _pollingTimer = new System.Timers.Timer(25);
            _pollingTimer.AutoReset = true;
            _pollingTimer.Elapsed += _pollingTimer_Elapsed;
            _pollingTimer.Enabled = true;
        }

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                XboxController.StopPolling();

                components.Dispose();
            }
            base.Dispose(disposing);
        }

        void _pollingTimer_Elapsed(object sender, System.Timers.ElapsedEventArgs e)
        {
            double L, R, C, f, t, s;
            CalcStrafeDrive(out L, out R, out C, out f, out t, out s);

            this.Invoke(new Action(() =>
            {
                txtL.Text = string.Format("{0:F4}", L);
                txtR.Text = string.Format("{0:F4}", R);
                txtC.Text = string.Format("{0:F4}", C);
                txtF.Text = string.Format("{0:F4}", f);
                txtT.Text = string.Format("{0:F4}", t);
                txtS.Text = string.Format("{0:F4}", s);
            }));
        }

        private double SignificantDigits(double value, int digitCount)
        {
            double factor = Math.Pow(10, digitCount);
            int trunc = (int)(factor * value);
            return trunc / factor;
        }

        // clamps to range, inclusive
        public double Clamp(double lowerBound, double value, double upperBound)
        {
            value = Math.Max(lowerBound, value);
            value = Math.Min(upperBound, value);
            return value;
        }

        public void CalcStrafeDrive(out double L, out double R, out double C, out double f, out double t, out double s)
        {
            const double Lfix = 1.0;
            const double Rfix = 1.0;
            const double Sfix = 1.0;

            var controller = XboxController.RetrieveController(0);

            f = Clamp(-1.0, controller.LeftThumbStick.Y / (double)short.MaxValue, 1.0);
            s = Clamp(-1.0, controller.LeftThumbStick.X / (double)short.MaxValue, 1.0);
            t = Clamp(-1.0, controller.RightThumbStick.X / (double)short.MaxValue, 1.0);

            f = SignificantDigits(f, 2);
            s = SignificantDigits(s, 2);
            t = SignificantDigits(t, 2);

            double fHalf = f * 0.5;
            double tHalf = t * 0.5;

            double numerL = fHalf * fHalf + tHalf * tHalf;
            double numerR = fHalf * fHalf - tHalf * tHalf;
            const double denom = 0.5 * 0.5 + 0.5 * 0.5;

            L = (numerL / denom) * Lfix;
            R = (numerR / denom) * Rfix;
            C = s * s * Sfix;

            L = SignificantDigits(L, 2);
            R = SignificantDigits(R, 2);
            C = SignificantDigits(C, 2);

            const double deadSpaceThreshold = 0.2;

            // gives us our dead space at rest
            if (t > -deadSpaceThreshold && t < deadSpaceThreshold && f > -deadSpaceThreshold && f < deadSpaceThreshold)
            {
                f = t = 0;
                L = R = 0;
            }

            // gives us our dead space at rest
            if (s > -deadSpaceThreshold && s < deadSpaceThreshold)
            {
                s = 0;
                C = 0;
            }

            // the following 'if' statements should be greater than some minimum threshold because a very small negative value,
            // even when the sticks are still, could cause the signs of the motor magnitudes to get messed up
            if (t <= -deadSpaceThreshold)
            {
                // swap L and R
                double temp = L;
                L = R;
                R = temp;
            }

            if (f <= -deadSpaceThreshold)
            {
                // invert the signs for L and R
                L = -L;
                R = -R;
            }

            if (s <= -deadSpaceThreshold)
            {
                // invert the sign for C
                C = -C;
            }
        }

    }
}
